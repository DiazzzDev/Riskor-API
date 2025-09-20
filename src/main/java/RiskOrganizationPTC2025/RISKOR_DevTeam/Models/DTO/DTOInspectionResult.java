package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOInspectionResult {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idInspectionResult;

    @NotNull
    private String idInspection;

    @NotNull
    private String idInspectionItem;

    @NotNull
    private String idComplianceStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
