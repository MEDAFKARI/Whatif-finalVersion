package ma.valueit.SfcBatchFtp.Config;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Service.FileService;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import ma.valueit.SfcBatchFtp.Util.FileModifier;
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
    private FileModifier fileModifier;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("XmlJob")
    private Job job;
    @Autowired
    private GlobalNameService globalNameService;
    @Autowired
    private FileService fileService;

    @Scheduled(cron = "${ScheduleCron}")
    public void importWithSchedule() {
        var fileName = "Input";
        System.out.println("Importe Done !!!!!");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .addString("output.file.name", "output/"+fileName)
                .toJobParameters();

        try {
            var jobExecution = jobLauncher.run(job, jobParameters);

            if (!jobExecution.getStatus().isUnsuccessful()) {
                System.out.println("Job Status: " + jobExecution.getStatus());

                System.out.println("Calling fileService.loadAll()");

                fileService.loadAll().forEach(path -> {
                    File file = new File("output/"+path);
                    System.out.println("From the Scheduler: " + file);

                    try {
                        fileModifier.modifyFile(file.getPath());
                    } catch (Exception e) {
                        System.err.println("Error sending file: " + file.getName() + ", " + e.getMessage());
                    }
                });
            } else {
                System.err.println("Job execution was unsuccessful.");
            }
        }catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();
        }

    }


}
