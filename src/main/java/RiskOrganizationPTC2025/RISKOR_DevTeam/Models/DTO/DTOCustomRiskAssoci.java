package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOCustomRiskAssoci {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idCustomRiskAssoci;

    //Regex para evitar que el nombre del riesgo asociado de la inspección sea como "%$,.321"
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ'’ ]{2,50}$",
            message = "El riesgo contiene carácteres no válidos"
    )
    @Size(min = 2, max = 50, message = "El título de la inspección debe contener entre 2 a 50 carácteres")
    @NotBlank(message = "El nombre del riesgo de la inspección personalizada es obligatorio")
    private String customRiskAssoci;

    @NotBlank
    private String idCustomTitleInsp;

    //Solo lectura para no permitir que el cliente cambie la empresa en el body
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}
