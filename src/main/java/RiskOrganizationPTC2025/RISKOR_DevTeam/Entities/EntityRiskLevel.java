package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBRISKLEVEL")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityRiskLevel {
    @Id @Column(name = "IDRISKLEVEL", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idRiskLevel;
    @Column(name = "RISKLEVELNAME", length = 50, nullable = false, updatable = false)
    private String riskLevelName;
}
