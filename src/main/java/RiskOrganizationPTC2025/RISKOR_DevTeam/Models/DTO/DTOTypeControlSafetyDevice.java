package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypeCategoryControlSD;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOTypeControlSafetyDevice {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID Será ignorado
    private String idTypeControlSD;

    @NotBlank @Size(max = 50, message = "Se han excedido la cantidad máxima de carácteres en los tipos de control de servicios.")
    private String typeControlSD;

    //Hacemos referencia a la OTRA ENTIDAD, la cuál contiene la clave primaria para relacionar la foránea
    @NotNull
    private String idTypeCategoryCSD;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
