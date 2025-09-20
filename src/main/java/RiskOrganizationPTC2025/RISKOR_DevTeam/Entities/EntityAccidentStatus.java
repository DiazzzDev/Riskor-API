package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestMapping;

@Entity
@Table(name = "TBACCIDENTSTATUS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAccidentStatus {
    @Id @Column(name = "IDACCIDENTSTATUS", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idAccidentStatus;
    @Column(name = "ACCIDENTSTATUS", length = 50, nullable = false, updatable = false)
    private String accidentStatus;
}
