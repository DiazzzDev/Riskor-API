package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBTYPEPERMISSION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityTypePermission {
    @Id @Column(name = "IDTYPEPERMISSION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idTypePermission;
    @Column(name = "PERMISSIONTYPE", length = 50, nullable = false, updatable = false)
    private String permissionType;
}
