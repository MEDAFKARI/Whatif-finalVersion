package ma.valueit.SfcBatchFtp.Config;


import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class IntegrationService {

    @Autowired
    @Qualifier("outboundChannel")
    private MessageChannel outboundChannel;

//    @Autowired
//    @Qualifier("inboundChannel")
//    private PollableChannel inboundChannel;

    @Autowired
    private SshCommandExecutor sshCommandExecutor;

    @Value("${script.path}")
    private String scriptPath;


    public void sendFile(File file) {
        Message<File> message = new GenericMessage<>(file);
        MessageChannel channel = outboundChannel;

        boolean fileSent = channel.send(message);

        if (fileSent) {
            System.out.println("File Sent!");
            System.out.println(file.getName());
            if (file.exists()){
                file.delete();
            }

        } else {
            System.out.println("File sending failed, not executing the script.");
        }
    }



//    public File getFile(){
//        Message<?> received = inboundChannel.receive();
//        return (File) received.getPayload();
//    }


}
