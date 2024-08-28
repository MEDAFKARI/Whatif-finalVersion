package ma.valueit.SfcBatchFtp.Config;
import com.jcraft.jsch.JSchException;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Configuration
public class SFTPIntegrationConfig {


    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("InsertingJob")
    private Job InsertingJob;

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.username}")
    private String username;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.remote}")
    private String remoteLocation;

    @Autowired
    private SshCommandExecutor sshCommandExecutor;

    @Value("${script.path}")
    private String scriptPath;


    @Autowired
    private GlobalNameService globalNameService;

    @Bean
    public MessageChannel outboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel archiveOutboundChannel() {
        return new DirectChannel();
    }


    @Bean
    public SessionFactory<SftpClient.DirEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory sf = new DefaultSftpSessionFactory(true);
        sf.setHost(host);
        sf.setPort(port);
        sf.setUser(username);
        sf.setPassword(password);
        sf.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(sf);
    }


    @Bean
    public SftpOutboundGateway sftpOutboundGateway(){
        SftpOutboundGateway gateway = new SftpOutboundGateway(sftpSessionFactory(), "put","payload");
        gateway.setAutoCreateDirectory(Boolean.TRUE);
        gateway.setRemoteDirectoryExpression(new LiteralExpression(remoteLocation));
        return gateway;
    }


    @Bean
    public SftpOutboundGateway ArchiveOutboutGateway(){
        SftpOutboundGateway gateway = new SftpOutboundGateway(sftpSessionFactory(), "put","payload");
        gateway.setAutoCreateDirectory(Boolean.TRUE);
        gateway.setRemoteDirectoryExpression(new LiteralExpression(remoteLocation+"/Archive"));
        return gateway;
    }

    @Bean
    public MessageChannel sftpArchiveOutputChannel() {
        return new DirectChannel();
    }


    @Bean
    public IntegrationFlow sftpArchiveOutboundFlow() {
        return IntegrationFlow.from(archiveOutboundChannel())
                .handle(ArchiveOutboutGateway())
                .channel(sftpArchiveOutputChannel())
                .get();
    }


    @Bean
    public IntegrationFlow archiveFileProcessingFlow() {
        return IntegrationFlow.from(sftpArchiveOutputChannel())
                .handle(message -> {
                    System.out.println("Archived file: " + message.getPayload());
                })
                .get();
    }




    @Bean
    public MessageChannel sftpOutputChannel() {
        return new DirectChannel();
    }



    @Bean
    public IntegrationFlow sftpOutboundFlow() {
        return IntegrationFlow.from(outboundChannel())
                .handle(sftpOutboundGateway())
                .channel(sftpOutputChannel())
                .get();
    }

    @Bean
    public IntegrationFlow handleSftpResponseFlow() {
        return IntegrationFlow.from(sftpOutputChannel())
                .handle(message -> {
                    System.out.println("Received response from SFTP: " + message.getPayload());
                    try {
                        sshCommandExecutor.executeScript(scriptPath, (String) message.getPayload());
                    } catch (JSchException | IOException e) {
                        throw new RuntimeException("Script execution failed", e);
                    }
                })
                .get();
    }




//    @Bean
//    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
//        System.out.println("Executed FROM SFTP ----------------------------");
//        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
//        synchronizer.setRemoteDirectory(remoteLocation);
//        synchronizer.setFilter(new SftpSimplePatternFileListFilter("output_" + globalNameService.getFile_Name() +"*"));
//        synchronizer.setDeleteRemoteFiles(true);
//        System.out.println("Executed");
//        System.out.println("---------------- FROM INBOUND FILE SYNC ----------------------");
//        System.out.println("output_" + globalNameService.getFile_Name());
////        synchronizer.setFilter(null);
//        return synchronizer;
//    }



    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
        System.out.println("Executing SFTP Inbound File Synchronizer ----------------------------");
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
        synchronizer.setRemoteDirectory(remoteLocation);
        synchronizer.setFilter(new SftpSimplePatternFileListFilter("XMLEVO_" + "*"));
        synchronizer.setDeleteRemoteFiles(true);
        return synchronizer;
    }

    @Bean
    public MessageSource<File> sftpMessageSource() {
        System.out.println("Setting up SFTP Message Source ----------------------------");
        SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer());
        source.setLocalDirectory(new File("output/"));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(null);

        return source;
    }

    @Bean
    public IntegrationFlow sftpInboundFlow() {
        return IntegrationFlow.from(sftpMessageSource(),
                        c -> c.poller(p -> p.fixedDelay(1000)))
                .handle(message -> {
                    System.out.println("------------SFTP INBOUND FLOW BEFORE PROCESSING---------");
                    File file = (File) message.getPayload();

                    System.out.println("Processing file: " + file.getName());

                    if (file.getName().startsWith("XMLEVO_")) {
                        System.out.println("File matches filter: " + file.getName());

                        JobParameters jobParameters = new JobParametersBuilder()
                                .addLong("startAt", System.currentTimeMillis())
                                .addString("output.file.path", file.getPath())
                                .addString("uniqueId", UUID.randomUUID().toString())
                                .toJobParameters();

                        try {
                            if (file.exists()) {
                                var jobExecution = jobLauncher.run(InsertingJob, jobParameters);
                                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                                    String timestamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
                                    String fileName = timestamp.concat("_Output.xml");
                                    File renamedFile = new File(file.getParent(), fileName);

                                    boolean renamed = file.renameTo(renamedFile);
                                    if (renamed) {
                                        System.out.println("Renamed file to: " + renamedFile.getPath());
                                        boolean sent = archiveOutboundChannel().send(new GenericMessage<>(renamedFile));
                                        if(sent){
                                            renamedFile.delete();
                                        }

                                    } else {
                                        System.out.println("File renaming failed!");
                                    }
                                } else {
                                    System.out.println("Job execution failed with status: " + jobExecution.getStatus());
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error executing job: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("File does not match filter, skipping: " + file.getName());
                    }
                })
                .get();
    }







}
