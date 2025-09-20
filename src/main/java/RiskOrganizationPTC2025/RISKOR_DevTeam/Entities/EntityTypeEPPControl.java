package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBTYPEEPPCONTROL")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityTypeEPPControl {
    @Id @Column(name = "IDTYPEEPPCONTROL", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idTypeEPPControl;

    @Column(name = "TYPEEPPCONTROL", length = 50, nullable = false)
    private String typeEPPControl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
