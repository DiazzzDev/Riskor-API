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
@Table(name = "TBTRAININGMODALITY")
@Getter @Setter @NotBlank @EqualsAndHashCode
public class EntityTrainingModality {
    @Id @Column(name = "IDTRAININGMODALITY", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idTrainingModality;
    @Column(name = "TRAININGMODALITY", length = 50, nullable = false, updatable = false)
    private String trainingModality;
}
