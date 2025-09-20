package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBINSPECTIONITEM")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityInspectionItem {
    @Id @Column(name = "IDINSPECTIONITEM", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idInspectionItem;

    @Column(name = "INSPECTIONTITLE", length = 75, nullable = false)
    private String inspectionTitle;

    @Column(name = "INSPECTIONEVIDENCE", length = 1000)
    private String inspectionEvidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
