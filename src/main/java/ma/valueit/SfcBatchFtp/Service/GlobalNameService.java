package ma.valueit.SfcBatchFtp.Service;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
@Service
public class GlobalNameService {

    private static String file_Name = "";


    public String getFile_Name() {
        return file_Name;
    }

    public void setFile_Name(String filename) {
        file_Name = filename;
    }
}
