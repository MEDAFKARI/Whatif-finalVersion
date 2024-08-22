package ma.valueit.SfcBatchFtp.DAO.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Output")

public class OutputEntity {
    @Id
    @Column(name = "CODE_EXERCICE", length = 4)
    private String codeExercice;

    @Column(name = "CODE_PERIODE", length = 2)
    private String codePeriode;

    @Column(name = "IE_AFFAIRE", length = 12)
    private String ieAffaire;

    @Column(name = "PERIODE")
    @Temporal(TemporalType.DATE)
    private Date periode;
}
