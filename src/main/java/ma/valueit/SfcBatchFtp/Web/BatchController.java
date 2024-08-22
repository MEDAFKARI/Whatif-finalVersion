package ma.valueit.SfcBatchFtp.Controller;

import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/batch")

public class BatchController {
    @Autowired
    private FtpService ftpService;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("XmlJob")
    private Job dbToXmlJob;

    @GetMapping("/run")
    public ResponseEntity<String> runBatchJob() {
        String timestamp = new SimpleDateFormat("yyyyMMddHH").format(new Date());
        var fileName = timestamp.concat("_Input.xml");
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("output.file.name", "output/"+fileName)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(dbToXmlJob, jobParameters);
            if (!jobExecution.getStatus().isUnsuccessful()){
                ftpService.uploadAllToFTP();
            }


            return ResponseEntity.ok("Batch job has been invoked: " + jobExecution.getStatus());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Batch job failed: " + e.getMessage());
        }
    }
}
