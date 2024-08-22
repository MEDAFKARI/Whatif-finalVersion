package ma.valueit.SfcBatchFtp.DAO.Repository;

import ma.valueit.SfcBatchFtp.Config.OutputProcessor;
import ma.valueit.SfcBatchFtp.DAO.Entity.OutputEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutputRepository extends JpaRepository<OutputEntity, String> {
}
