package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOCustomTitleInspection {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idCustomTitleInsp;

    //Regex para evitar que el título de la inspección sea como "%$,.3"
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ'’ ]{5,100}$",
            message = "El título contiene carácteres no válidos"
    )
    @Size(min = 5, max = 100, message = "El título de la inspección debe contener entre 5 a 100 carácteres")
    @NotBlank(message = "El título de la inspección personalizada es obligatorio")
    private String customTitleInsp;

    //Solo lectura para no permitir que el cliente cambie la empresa en el body
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}
