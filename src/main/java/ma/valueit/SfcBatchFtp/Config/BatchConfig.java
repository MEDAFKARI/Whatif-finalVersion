package ma.valueit.SfcBatchFtp.Config;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.DAO.Entity.InputEntity;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
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
import java.util.Date;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;


//    @Bean
//    public MultiResourceItemWriter<InputEntity> multiResourceItemWriter() throws IOException {
//        return new MultiResourceItemWriterBuilder<InputEntity>()
//                .name("multiResourceItemWriter")
//                .resource(new FileSystemResource("output/data"))
//                .itemCountLimitPerResource(2)
//                .delegate(xmlItemWriter(null))
//                .build();
//    }



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
                .fetchSize(1000)
                .build();
    }

    @Bean
    public InputProcessor processor() {
        return new InputProcessor();
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<InputEntity> xmlItemWriter(@Value("#{jobParameters['output.file.name']}") String filename) throws IOException {
        File file = new File(filename);
        file.getParentFile().mkdirs();

        System.out.println(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create new file", e);
            }
        }

        StaxEventItemWriter<InputEntity> writer = new StaxEventItemWriter<>();
        writer.setResource(new FileSystemResource(file));


        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(InputEntity.class);
        writer.setMarshaller(marshaller);
        writer.setRootTagName("StrategyOneRequest");
        writer.setOverwriteOutput(Boolean.TRUE);

        return writer;
    }

    @Bean
    public Step WritingStep() throws IOException {
        return new StepBuilder("Xml File Writer Step", jobRepository)
                .<InputEntity, InputEntity>chunk(10, platformTransactionManager)
                .reader(DataReader())
                .processor(processor())
                .writer(xmlItemWriter(null))
                .build();
    }

    @Bean
    @Qualifier("XmlJob")
    public Job dbToXml() throws IOException {
        return new JobBuilder("XmlJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(WritingStep())
                .build();
    }

}
