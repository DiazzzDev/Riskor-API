package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

//Guardar una locación
@Getter @Setter
public class DTOLocationCreate {
    @NotBlank(message = "El nombre de la ubicación es obligatorio")
    @Size(min = 4, max = 125, message = "El nombre de la locación debe tener de 4 a 125 carácteres")
    private String locationName;
}
