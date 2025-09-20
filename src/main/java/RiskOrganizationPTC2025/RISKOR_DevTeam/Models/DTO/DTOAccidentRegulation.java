package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRegulationBusiness;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAccidentRegulation {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idAccidentRegulation;

    @NotNull(message = "idAccident es obligatorio")
    private String idAccident;

    @NotNull(message = "idRegulation es obligatorio")
    private String idRegulation;

    // Solo lectura para no permitir que el cliente cambie la empresa en el body
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}
