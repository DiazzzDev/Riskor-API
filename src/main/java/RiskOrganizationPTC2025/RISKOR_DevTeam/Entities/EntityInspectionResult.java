package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBINSPECTIONRESULT")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityInspectionResult {
    @Id @Column(name = "IDINSPECTIONRESULT", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idInspectionResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDINSPECTION")
    private EntityInspection idInspection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCOMPLIANCESTATUS")
    private EntityComplianceStatus idComplianceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
