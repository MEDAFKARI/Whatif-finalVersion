package ma.valueit.SfcBatchFtp.Config;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class SshCommandExecutor {

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.username}")
    private String username;
    @Value("${sftp.password}")
    private String password;


    public void executeScript(String scriptPath, String fileName) throws JSchException, IOException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(username,host,port);
        session.setPassword(password);

        Properties config = new Properties();

        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");

        channelExec.setCommand("bash " + scriptPath + " "+fileName);
        channelExec.setErrStream(System.err);
        InputStream in = channelExec.getInputStream();
        channelExec.connect();


        byte[] tmp = new byte[1024];

        StringBuilder outputBuffer = new StringBuilder();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                String output = new String(tmp, 0, i);
                System.out.print(output);
                outputBuffer.append(output);
            }
            if (channelExec.isClosed()) {
                if (in.available() > 0) continue;
                System.out.println("Exit status: " + channelExec.getExitStatus());
                break;
            }
        }
        System.out.println("Full script output:\n" + outputBuffer);

        channelExec.disconnect();
        session.disconnect();




    }



}
