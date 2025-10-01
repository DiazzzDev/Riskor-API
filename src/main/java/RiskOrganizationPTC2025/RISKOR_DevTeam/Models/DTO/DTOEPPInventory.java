package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOEPPInventory {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID en POST Será ignorado
    private String idEPPInventory;

    @NotBlank @Size(min = 2, max = 100, message = "El nombre del equipo de protección personal debe contener de 2 a 100 carácteres")
    private String nameEPP;

    @NotBlank @Size(min = 10, max = 255, message = "La descripción del EPP debe contener de 10 a 255 carácteres")
    private String description;

    @NotNull @PositiveOrZero(message = "La cantidad total debe ser positiva o cero")
    private int totalQuantity;

    @NotNull @PositiveOrZero(message = "La cantidad disponible debe ser positiva o cero")
    private int availableQuantity;

    @NotNull
    private String idTypeEPPControl;

    private String typeEPPControl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
