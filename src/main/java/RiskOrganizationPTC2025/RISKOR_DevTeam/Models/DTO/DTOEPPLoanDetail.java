package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOEPPLoanDetail {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idEPPLoanDetail;

    @NotNull @Min(value = 0, message = "La cantidad entregada no puede ser negativa")
    private int quantityDelivered;

    @NotNull @Min(value = 0, message = "La cantidad devuelta no puede ser negativa")
    private int quantityReturned;

    @NotNull
    private String idEPPLoan;

    @NotNull
    private String idEPPInventory;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
