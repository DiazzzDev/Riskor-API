package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBCUSTOMRISKASSOCI")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityCustomRiskAssoci {
    @Id @Column(name = "IDCUSTOMRISKASSOCI", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idCustomRiskAssoci;

    @Column(name = "CUSTOMRISKASSOCI", length = 50, nullable = false)
    private String customRiskAssoci;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCUSTOMTITLEINSP", nullable = false)
    private EntityCustomTitleInspection idCustomTitleInsp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo  idBusiness;
}
