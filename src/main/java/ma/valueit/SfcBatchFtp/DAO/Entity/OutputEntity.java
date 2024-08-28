package ma.valueit.SfcBatchFtp.DAO.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Output")
public class OutputEntity {

    @Column(name = "CODE_EXERCICE", length = 4)
    private String codeExercice;


    @Column(name = "CODE_PERIODE", length = 2)
    private String codePeriode;

    @Id
    @Column(name = "IE_AFFAIRE", length = 12)
    private String ieAffaire;

    @Column(name = "PERIODE")
    @Temporal(TemporalType.DATE)
    private Date periode;
}
