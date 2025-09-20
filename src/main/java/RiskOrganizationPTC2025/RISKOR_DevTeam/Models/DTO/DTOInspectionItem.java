package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter @Setter
public class DTOInspectionItem {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idInspectionItem;

    @NotBlank @Size(min = 3, max = 75, message = "Se requiere un título")
    private String inspectionTitle;

    @Nullable
    @Size(max = 1000, message = "Evidencia es requerida para la inspección")
    private String inspectionEvidence;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
