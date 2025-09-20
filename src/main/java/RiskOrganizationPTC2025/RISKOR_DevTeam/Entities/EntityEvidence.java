package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBEVIDENCE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityEvidence {

    @Id @Column(name = "IDEVIDENCE", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idEvidence;

    @Column(name = "ACCIDENTEVIDENCE", nullable = false, length = 1000)
    private String accidentEvidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENT", nullable = false)
    private EntityAccident idAccident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
