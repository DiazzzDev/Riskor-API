package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOTypeEPPControl {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID Será ignorado
    private String idTypeEPPControl;

    @NotBlank @Size(max = 50, message = "Se han excedido la cantidad máxima de carácteres en tipos de equipo de protección personal.")
    private String typeEPPControl;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
