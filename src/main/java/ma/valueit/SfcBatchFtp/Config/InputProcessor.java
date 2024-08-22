package ma.valueit.SfcBatchFtp.Config;

import ma.valueit.SfcBatchFtp.DAO.Entity.InputEntity;
import org.springframework.batch.item.ItemProcessor;

public class InputProcessor implements ItemProcessor<InputEntity, InputEntity> {
    @Override
    public InputEntity process(InputEntity item) throws Exception {
        return item;
    }
}
