package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBTYPECONTROLSAFETYDEVICE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityTypeControlSafetyDevice {
    @Id @Column(name = "IDTYPECONTROLSD", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idTypeControlSD;

    @Column(name = "TYPECONTROLSD", length = 50, nullable = false)
    private String typeControlSD;

    //Hacemos referencia a la OTRA ENTIDAD, la cuál contiene la clave primaria para relacionar la foránea
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTYPECATEGORYCSD", nullable = false)
    private EntityTypeCategoryControlSD idTypeCategoryCSD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
