package ma.valueit.SfcBatchFtp.Service;
import org.apache.catalina.webresources.FileResource;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FtpService {

    private final Path root = Paths.get("output");


    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.remote}")
    private String remoteLocation;


    public FTPClient configureFTPClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host,port);
        ftpClient.login(username, password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }


    public void uploadFile(MultipartFile inputStream) throws IOException {
        FTPClient ftpClient = configureFTPClient();
        String FileLocation = remoteLocation + inputStream.getOriginalFilename();

        try {
            boolean result = ftpClient.storeFile(FileLocation, inputStream.getInputStream());
            if (!result) {
                throw new IOException("Couldn't upload file");
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }


    public void uploadFileInputStream(File file) throws IOException {
        FTPClient ftpClient = configureFTPClient();
        String FileLocation = remoteLocation + file.getName();

        try {
            boolean result = ftpClient.storeFile(FileLocation, new FileInputStream(file));
            if (!result) {
                throw new IOException("Couldn't upload file");
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                delete(FileLocation);
            }
        }
    }

    public boolean delete(String filename) {
        System.out.println(filename);
        try {
            Path file = root.resolve(filename);
            Thread.sleep(500);
            System.out.println(file);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Void uploadAllToFTP() {
        try {
            Files.walk(this.root, 1)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            // Upload the file
                            uploadFileInputStream(file.toFile());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to upload or delete file: " + file.toString(), e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!", e);
        }
        return null;
    }


}




