package ma.valueit.SfcBatchFtp.Config;


import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.DAO.Entity.OutputEntity;
import ma.valueit.SfcBatchFtp.DAO.Repository.OutputRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WritingToDbBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OutputRepository outputRepository;
    private final DataSource dataSource;


    @Bean
    public OutputProcessor outputProcessor(){
        return new OutputProcessor();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<OutputEntity> reader(@Value("#{jobParameters['output.file.path']}") String filePath) {
        System.out.println("THE POINTER ON THE READER -----------------------------------------");
        System.out.println("The file Path is : " + filePath);
        return new StaxEventItemReaderBuilder<OutputEntity>()
                .name("Reader")
                .resource(new FileSystemResource(filePath))
                .addFragmentRootElements("Output")
                .unmarshaller(new Jaxb2Marshaller() {
                    {
                        setClassesToBeBound(OutputEntity.class);
                    }
                })
                .build();
    }



    @Bean
    public ItemWriter<OutputEntity> outputWriter() {
        System.out.println("THE POINTER ON THE WRITERRRR -----------------------------------------");
        RepositoryItemWriter<OutputEntity> writer = new RepositoryItemWriter<>();
        writer.setMethodName("save");
        writer.setRepository(outputRepository);
        return writer;
    }



    @Bean
    public Step insertStep() {
        System.out.println("THE POINTER ON THE STEPPP -----------------------------------------");

        return new StepBuilder("insertToDb", jobRepository)
                .<OutputEntity, OutputEntity>chunk(1000, platformTransactionManager)
                .reader(reader(null))
                .processor(outputProcessor())
                .writer(outputWriter())
                .build();
    }


    @Bean
    @Qualifier("InsertingJob")
    public Job InsertingJob(){
        System.out.println("JOB EXECUTED -----------------------------------------");
        return new JobBuilder("InsertJob",jobRepository)
                    .start(insertStep())
                    .build();
    }



}
