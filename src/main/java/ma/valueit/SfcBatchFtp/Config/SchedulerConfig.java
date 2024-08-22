package ma.valueit.SfcBatchFtp.Config;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SchedulerConfig {
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("XmlJob")
    private Job job;
    @Autowired
    private GlobalNameService globalNameService;

    @Scheduled(cron = "${ScheduleCron}")
    public void importWithSchedule() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        var fileName = timestamp.concat("_Input.xml");
        System.out.println("Importe Done !!!!!");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .addString("output.file.name", "output/"+fileName)
                .toJobParameters();

        globalNameService.setFile_Name(fileName);

        try {
            var jobExecution = jobLauncher.run(job, jobParameters);
            if (!jobExecution.getStatus().isUnsuccessful()) {
                System.out.println(jobExecution.getStatus());
                File file = new File(jobParameters.getString("output.file.name"));
                System.out.println("From the Scheduler : " + file);
                integrationService.sendFile(file);
                System.out.println("This from Scheduler :"+globalNameService.getFile_Name());
            }


        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();
        }

    }


}
