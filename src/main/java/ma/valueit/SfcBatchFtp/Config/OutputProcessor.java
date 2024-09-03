package ma.valueit.SfcBatchFtp.Config;

import ma.valueit.SfcBatchFtp.DAO.Entity.OutputEntity;
import org.springframework.batch.item.ItemProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputProcessor implements ItemProcessor<OutputEntity, OutputEntity> {

    @Override
    public OutputEntity process(OutputEntity item) throws Exception {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date());
        item.setExecutionTime(timestamp);
        return item;
    }
}
