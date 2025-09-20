package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBMEDICALSTATUS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityMedicalStatus {
    @Id @Column(name = "IDMEDICALSTATUS", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idMedicalStatus;
    @Column(name = "MEDICALSTATUS", length = 50, nullable = false, updatable = false)
    private String medicalStatus;
}
