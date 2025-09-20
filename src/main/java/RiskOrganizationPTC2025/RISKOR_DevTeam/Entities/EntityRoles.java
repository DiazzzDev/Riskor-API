package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBROLE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityRoles {
    @Id @Column(name = "IDROLE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idRole;
    @Column(name = "ROLENAME", length = 35, nullable = false, updatable = false) //No se puede actualizar, solo insertar (Por primer uso nada más)
    private String roleName;
}
