package ma.valueit.SfcBatchFtp.Web;


import lombok.RequiredArgsConstructor;
import ma.valueit.SfcBatchFtp.Service.FtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ftp")
public class FtpController {

    private final FtpService ftpService;

    @PostMapping("/Upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file){

        try {
            ftpService.uploadFile(file);
            return ResponseEntity.status(HttpStatus.OK).body("Uploaded!");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot Upload File!");
        }

    }


}
