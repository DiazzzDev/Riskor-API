package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBTYPECATEGORYCONTROLSD")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityTypeCategoryControlSD {
    @Id @Column(name = "IDTYPECATEGORYCSD", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idTypeCategoryCSD;
    @Column(name = "TYPECATEGORYCSD", length = 50, nullable = false, updatable = false)
    private String typeCategoryCSD;
}
