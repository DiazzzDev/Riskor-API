package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOEvidence {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idEvidence;

    @NotBlank(message = "La evidencia del accidente es obligatoria")
    private String accidentEvidence;

    @NotBlank(message = "idAccident es obligatorio")
    private String idAccident;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}
