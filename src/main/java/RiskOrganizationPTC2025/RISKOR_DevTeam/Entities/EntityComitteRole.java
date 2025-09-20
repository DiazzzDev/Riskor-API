package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TBCOMITTEROLE")
@Getter @Setter @NotBlank @EqualsAndHashCode
public class EntityComitteRole {
    @Id @Column(name = "IDCOMITTEROLE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idRole;
    @Column(name = "COMITTEROLENAME", length = 50, nullable = false, updatable = false)
    private String committeRoleName;
}
