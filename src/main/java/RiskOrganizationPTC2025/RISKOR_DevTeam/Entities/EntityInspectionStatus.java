package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBINSPECTIONSTATUS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityInspectionStatus {
    @Id @Column(name = "IDINSPECTIONSTATUS", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idInspectionStatus;
    @Column(name = "INSPECTIONSTATUS", length = 50, nullable = false, updatable = false)
    private String inspectionStatus;
}
