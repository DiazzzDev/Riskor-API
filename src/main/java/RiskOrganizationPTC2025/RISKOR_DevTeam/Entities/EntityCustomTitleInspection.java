package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBCUSTOMTITLEINSPECTION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityCustomTitleInspection {
    @Id @Column(name = "IDCUSTOMTITLEINSP", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idCustomTitleInsp;

    @Column(name = "CUSTOMTITLEINSP", length = 100, nullable = false)
    private String customTitleInsp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo  idBusiness;
}
