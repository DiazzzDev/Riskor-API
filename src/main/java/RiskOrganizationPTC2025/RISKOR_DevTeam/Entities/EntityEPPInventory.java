package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "TBEPPINVENTORY")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityEPPInventory {

    @Id @Column(name = "IDEPPINVENTORY", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private String idEPPInventory;
    @Column(name = "NAMEEPP")
    private String nameEPP;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TOTALQUANTITY")
    private int totalQuantity;
    @Column(name = "AVAILABLEQUANTITY")
    private int availableQuantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTYPEEPPCONTROL")
    private EntityTypeEPPControl idTypeEPPControl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
