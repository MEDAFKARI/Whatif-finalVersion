package ma.valueit.SfcBatchFtp.DAO.Repository;

import ma.valueit.SfcBatchFtp.DAO.Entity.InputEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InputRepository extends JpaRepository<InputEntity, String> {
}
