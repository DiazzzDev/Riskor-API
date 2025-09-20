package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBACCIDENTREGULATION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAccidentRegulation {
    @Id @Column(name = "IDACCIDENTREGULATION", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idAccidentRegulation;

    @ManyToOne @JoinColumn(name = "IDACCIDENT", nullable = false)
    private EntityAccident idAccident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDREGULATION", nullable = false)
    private EntityRegulationBusiness idRegulation;

    @ManyToOne @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
