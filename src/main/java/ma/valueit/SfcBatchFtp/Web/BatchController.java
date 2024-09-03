package ma.valueit.SfcBatchFtp.Controller;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Config.IntegrationService;
import ma.valueit.SfcBatchFtp.Service.FileService;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import ma.valueit.SfcBatchFtp.Service.GlobalNameService;
import ma.valueit.SfcBatchFtp.Util.FileModifier;
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

    @Autowired
    private FileModifier fileModifier;
    @Autowired
    private FileService fileService;


    @GetMapping("/run")
    public ResponseEntity<String> runBatchJob() {
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

            }
            return ResponseEntity.ok("Batch job has been invoked: " + jobExecution.getStatus());
        }catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Batch job failed: " + e.getMessage());
        }

    }
}



