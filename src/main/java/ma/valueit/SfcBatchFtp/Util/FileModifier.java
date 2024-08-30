package ma.valueit.SfcBatchFtp.Util;

import ma.valueit.SfcBatchFtp.Config.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class FileModifier {

    @Autowired
    private IntegrationService integrationService;

    public void modifyFile(String filePath) throws IOException {
        File originalFile = new File(filePath);
        File xsltFile = new File("src/main/resources/templates/remove-a.xslt");
        File outputFile = new File("output/Sample");

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = factory.newTransformer(new StreamSource(xsltFile));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }

        try {
            transformer.transform(new StreamSource(originalFile), new StreamResult(outputFile));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Removed <a> tags using XSLT and saved to output.xml");


        String content = new String(Files.readAllBytes(Paths.get(outputFile.getAbsolutePath())));

        if (content.startsWith("<?xml")) {
            content = content.substring(content.indexOf(">") + 1);
        }

        Files.write(Paths.get(outputFile.getAbsolutePath()), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("XML declaration removed.");

        integrationService.sendFile(outputFile);
        originalFile.delete();
    }
}
