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
@Table(name = "TBREGULATIONCATEGORY")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityRegulationCategory {
    @Id @Column(name = "IDREGULATIONCATEGORY", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idRegulationCategory;
    @Column(name = "REGULATIONCATEGORY", length = 50, nullable = false, updatable = false)
    private String regulationCategory;
}
