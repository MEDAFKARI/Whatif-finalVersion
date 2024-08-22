package ma.valueit.SfcBatchFtp.Config;

import ma.valueit.SfcBatchFtp.DAO.Entity.OutputEntity;
import org.springframework.batch.item.ItemProcessor;

public class OutputProcessor implements ItemProcessor<OutputEntity, OutputEntity> {

    @Override
    public OutputEntity process(OutputEntity item) throws Exception {
        return item;
    }
}
