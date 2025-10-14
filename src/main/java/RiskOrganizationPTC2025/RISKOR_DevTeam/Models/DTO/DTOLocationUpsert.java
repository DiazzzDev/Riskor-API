package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOLocationUpsert {
    /**
     * Si viene: el cliente indica que esta locación ya existe (usar este id)
     * Si es nulo: backend intentará encontrar por name y si no existe la creará.
     */
    private String idLocation;

    @NotBlank(message = "El nombre de la ubicación es obligatorio")
    @Size(min = 4, max = 125, message = "El nombre de la locación debe tener de 4 a 125 carácteres")
    private String locationName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idArea;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}