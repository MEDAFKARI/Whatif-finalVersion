package ma.valueit.SfcBatchFtp.Config;

import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.DAO.Entity.InputEntity;
import ma.valueit.SfcBatchFtp.DAO.Entity.StrategyOneRequest;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;



    @Bean
    public JdbcCursorItemReader<InputEntity> DataReader(){
        return new JdbcCursorItemReaderBuilder<InputEntity>()
                .dataSource(dataSource)
                .name("Data Reader")
//                .sql("SELECT * FROM DBISIC.ICT_ENCOURS_BRUT\n" +
//                        "WHERE TRUNC(INSERT_DATE, 'MM') = (\n" +
//                        "    SELECT MAX(TRUNC(INSERT_DATE, 'MM'))\n" +
//                        "    FROM DBISIC.ICT_ENCOURS_BRUT\n" +
//                        ")")
                .sql("Select * from ICT_ENCOURS_BRUT")
                .rowMapper(new DataClassRowMapper<>(InputEntity.class))
                .build();
    }

    @Bean
    public InputProcessor inputProcessor() {
        return new InputProcessor();
    }

//    @Bean
//    public ItemProcessor<InputEntity, StrategyOneRequest> xmlStructureProcessor() {
//        return inputEntity -> {
//            StrategyOneRequest request = new StrategyOneRequest();
//            request.setHeader(new StrategyOneRequest.Header());
//            request.setBody(inputEntity);
//            return request;
//        };
//    }

//    @Bean
//    public CompositeItemProcessor<InputEntity, StrategyOneRequest> compositeProcessor() {
//        CompositeItemProcessor<InputEntity, StrategyOneRequest> compositeProcessor = new CompositeItemProcessor<>();
//        compositeProcessor.setDelegates(Arrays.asList(
//                inputProcessor(),
//                xmlStructureProcessor()
//        ));
//        return compositeProcessor;
//    }


    @Bean
    @StepScope
    public MultiResourceItemWriter<InputEntity> multiXmlItemWriter(@Value("#{jobParameters['output.file.name']}") String filename) throws IOException {
        return new MultiResourceItemWriterBuilder<InputEntity>()
                .name("multiResourceItemWriter")
                .resource(new FileSystemResource(filename))
                .resourceSuffixCreator(resourceSuffixCreator())
                .itemCountLimitPerResource(5)
                .delegate(xmlItemWriter(filename))
                .build();
    }

    @Bean
    public ResourceSuffixCreator resourceSuffixCreator() {
        return new ResourceSuffixCreator() {
            @Override
            public String getSuffix(int index) {
                return index + ".xml";
            }
        };
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<InputEntity> xmlItemWriter(@Value("#{jobParameters['output.file.name']}") String filename) throws IOException {
        StaxEventItemWriter<InputEntity> writer = new StaxEventItemWriter<>();
        writer.setResource(new FileSystemResource(filename));
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(InputEntity.class);
        writer.setMarshaller(marshaller);
        writer.setRootTagName("a");
        writer.setOverwriteOutput(true);
        return writer;
    }



    @Bean
    public Step writingStep() throws IOException {
        return new StepBuilder("XmlFileWriterStep", jobRepository)
                .<InputEntity, InputEntity>chunk(5, platformTransactionManager)
                .reader(DataReader())
                .processor(inputProcessor())
                .writer(multiXmlItemWriter(null))
                .build();
    }


    @Bean
    @Qualifier("XmlJob")
    public Job dbToXml() throws IOException {
        return new JobBuilder("XmlJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(writingStep())
                .build();
    }

}
