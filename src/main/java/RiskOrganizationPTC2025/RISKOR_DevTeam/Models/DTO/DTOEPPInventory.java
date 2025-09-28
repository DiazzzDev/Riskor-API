package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOEPPInventory {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID en POST Será ignorado
    private String idEPPInventory;
    @NotBlank
    private String nameEPP;
    @NotBlank
    private String description;
    @NotNull
    private int totalQuantity;
    @NotNull
    private int availableQuantity;
    @NotNull
    private String idTypeEPPControl;

    private String typeEPPControl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
