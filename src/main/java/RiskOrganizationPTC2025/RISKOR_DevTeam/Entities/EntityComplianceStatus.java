package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBCOMPLIANCESTATUS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityComplianceStatus {
    @Id @Column(name = "IDCOMPLIANCESTATUS", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idComplianceStatus;
    @Column(name = "COMPLIANCESTATUS", length = 50, nullable = false, updatable = false)
    private String complianceStatus;
}
