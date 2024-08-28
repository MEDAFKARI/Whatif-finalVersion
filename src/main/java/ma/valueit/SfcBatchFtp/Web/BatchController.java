package ma.valueit.SfcBatchFtp.Controller;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Config.IntegrationService;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/batch")

public class BatchController {

    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("XmlJob")
    private Job job;
    @Autowired
    private GlobalNameService globalNameService;

    @GetMapping("/run")
    public ResponseEntity<String> runBatchJob() {
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

            return ResponseEntity.ok("Batch job has been invoked: " + jobExecution.getStatus());


        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Batch job failed: " + e.getMessage());
        }

    }
}
