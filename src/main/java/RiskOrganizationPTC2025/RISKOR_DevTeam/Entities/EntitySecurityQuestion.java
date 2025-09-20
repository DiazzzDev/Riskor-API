package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBSECURITYQUESTION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntitySecurityQuestion {
    @Id @Column(name = "IDQUESTION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idQuestion;
    @Column(name = "NAMEQUESTION", length = 125, nullable = false)
    private String nameQuestion;
}
