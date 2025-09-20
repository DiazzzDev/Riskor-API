package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOArea {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID en POST Será ignorado
    private String idArea;
    @NotBlank
    @Size(min = 5, max = 75, message = "El nombre del área debe tener de 5 a 75 carácteres")
    private String areaName;
    @NotBlank
    @Size(min = 5, max = 1000, message = "El enlace de la imágen de área debe tener de 5 a 1000 carácteres")
    private String areaSketch;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
