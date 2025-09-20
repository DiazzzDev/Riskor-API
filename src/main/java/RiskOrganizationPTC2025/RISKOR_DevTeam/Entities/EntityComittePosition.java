package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBCOMITTEPOSITION")
@Getter @Setter @NotBlank @ToString @EqualsAndHashCode
public class EntityComittePosition {
    @Id @Column(name = "IDCOMITTEPOSITION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idComitteP;
    @Column(name = "COMITTEPOSITONNAME", length = 50, nullable = false, updatable = false)
    private String committePositionName;
}
