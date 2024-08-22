package ma.valueit.SfcBatchFtp.Config;


import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
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
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.messaging.MessageChannel;

import java.io.File;

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
    private GlobalNameService globalNameService;

    @Bean
    public MessageChannel outboundChannel() {
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
                })
                .get();
    }


    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
        System.out.println("Executed FROM SFTP ----------------------------");
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
        synchronizer.setDeleteRemoteFiles(false);
        synchronizer.setRemoteDirectory(remoteLocation);
        synchronizer.setFilter(new SftpSimplePatternFileListFilter("output_" + globalNameService.getFile_Name() +"*"));
        System.out.println("Executed");
        System.out.println("---------------- FROM INBOUND FILE SYNC ----------------------");
        System.out.println("output_" + globalNameService.getFile_Name());
//        synchronizer.setFilter(null);
        return synchronizer;
    }

    @Bean
    public MessageSource<File> sftpMessageSource() {
        System.out.println("Executed ----------------------------");
        SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer());
        source.setLocalDirectory(new File("output/"));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<>());
        return source;
    }

    @Bean
    public IntegrationFlow sftpInboundFlow() {
        return IntegrationFlow.from(sftpMessageSource(),
                        c -> c.poller(p -> p.fixedDelay(500)))
                .handle(message -> {
                    File file = (File) message.getPayload();
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addLong("startAt", System.currentTimeMillis())
                            .addString("output.file.path", file.getPath())
                            .toJobParameters();
                    try {
                        var jobexecution = jobLauncher.run(InsertingJob, jobParameters);
                        if (!jobexecution.getStatus().isUnsuccessful()){
                            System.out.println("Done Execution");
                        }
                    } catch (JobExecutionAlreadyRunningException | JobRestartException |
                             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Received file: " + file.getName());
                    System.out.println("Received file: " + file.getPath());

                })
                .get();
    }









}
