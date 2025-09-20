package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBINSPECTIONTYPE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityInspectionType {
    @Id @Column(name = "IDINSPECTIONTYPE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idInspectionType;
    @Column(name = "INSPECTIONTYPE", length = 50, nullable = false, updatable = false)
    private String inspectionType;
}
