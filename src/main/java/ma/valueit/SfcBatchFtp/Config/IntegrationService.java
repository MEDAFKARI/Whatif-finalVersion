package ma.valueit.SfcBatchFtp.Config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class IntegrationService {

    @Autowired
    @Qualifier("outboundChannel")
    private MessageChannel outboundChannel;

//    @Autowired
//    @Qualifier("inboundChannel")
//    private PollableChannel inboundChannel;


    public void sendFile(File file){
        outboundChannel.send(new GenericMessage<>(file));
        System.out.println("File Sent!");
        if (file.exists()){
            file.delete();
        }
    }


//    public File getFile(){
//        Message<?> received = inboundChannel.receive();
//        return (File) received.getPayload();
//    }


}
