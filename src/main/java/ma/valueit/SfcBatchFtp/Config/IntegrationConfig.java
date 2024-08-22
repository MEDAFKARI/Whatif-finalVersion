package ma.valueit.SfcBatchFtp.Config;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.filters.FtpSimplePatternFileListFilter;
import org.springframework.integration.ftp.gateway.FtpOutboundGateway;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizingMessageSource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.messaging.MessageChannel;

import java.io.File;

@Configuration
public class IntegrationConfig {

//    @Autowired
//    private JobLauncher jobLauncher;
//
//    @Autowired
//    @Qualifier("InsertingJob")
//    private Job InsertingJob;
//
//
//    @Value("${ftp.host}")
//    private String host;
//    @Value("${ftp.port}")
//    private int port;
//    @Value("${ftp.username}")
//    private String username;
//    @Value("${ftp.password}")
//    private String password;
//    @Value("${ftp.remote}")
//    private String remoteLocation;
//
//
//    @Autowired
//    private GlobalNameService globalNameService;
//
//
//
//    @Bean
//    public MessageChannel outboundChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public SessionFactory<FTPFile> ftpSessionFactory(){
//        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
//        sf.setHost(host);
//        sf.setPort(port);
//        sf.setUsername(username);
//        sf.setPassword(password);
//        sf.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
//        return new CachingSessionFactory<>(sf);
//    }
//
//    @Bean
//    public FtpOutboundGateway ftpOutboundGateway(){
//        FtpOutboundGateway gateway = new FtpOutboundGateway(ftpSessionFactory(), "put","payload");
//        gateway.setAutoCreateDirectory(Boolean.TRUE);
//        gateway.setRemoteDirectoryExpression(new LiteralExpression(remoteLocation));
//        return gateway;
//    }
//
//    @Bean
//    public MessageChannel ftpOutputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public IntegrationFlow outboundFlow() {
//        return IntegrationFlow.from(outboundChannel())
//                .handle(ftpOutboundGateway())
//                .channel(ftpOutputChannel())
//                .get();
//    }
//
//    @Bean
//    public IntegrationFlow handleFtpResponseFlow() {
//        return IntegrationFlow.from(ftpOutputChannel())
//                .handle(message -> {
//                    System.out.println("Received response from FTP: " + message.getPayload());
//                })
//                .get();
//    }
//
//
//    @Bean
//    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
//        System.out.println("Executed ----------------------------");
//        FtpInboundFileSynchronizer synchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
//        synchronizer.setDeleteRemoteFiles(false);
//        synchronizer.setRemoteDirectory(remoteLocation);
//        synchronizer.setFilter(new FtpSimplePatternFileListFilter("output_" + globalNameService.getFile_Name() +"*"));
//        System.out.println("Executed");
//        System.out.println("---------------- FROM INBOUND FILE SYNC ----------------------");
//        System.out.println("output_" + globalNameService.getFile_Name());
////        synchronizer.setFilter(null);
//        return synchronizer;
//    }
//
//    @Bean
//    public MessageSource<File> ftpMessageSource() {
//        System.out.println("Executed ----------------------------");
//        FtpInboundFileSynchronizingMessageSource source = new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
//        source.setLocalDirectory(new File("output/"));
//        source.setAutoCreateLocalDirectory(true);
//        source.setLocalFilter(new AcceptOnceFileListFilter<>());
//        return source;
//    }
//
//    @Bean
//    public IntegrationFlow ftpInboundFlow() {
//        return IntegrationFlow.from(ftpMessageSource(),
//                        c -> c.poller(p -> p.fixedDelay(500)))
//                .handle(message -> {
//                    File file = (File) message.getPayload();
//
//                    JobParameters jobParameters = new JobParametersBuilder()
//                            .addLong("startAt", System.currentTimeMillis())
//                            .addString("output.file.path", file.getPath())
//                            .toJobParameters();
//                    try {
//                        var jobexecution = jobLauncher.run(InsertingJob, jobParameters);
//                        if (!jobexecution.getStatus().isUnsuccessful()){
//                            System.out.println("Done Execution");
//                        }
//                    } catch (JobExecutionAlreadyRunningException | JobRestartException |
//                             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println("Received file: " + file.getName());
//                    System.out.println("Received file: " + file.getPath());
//
//                })
//                .get();
//    }
//


}
