package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOEPPLoan {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idEPPLoan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate loanStartDate;

    @NonNull
    @FutureOrPresent(message = "No se puede devolver en el pasado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate loanReturnDate;

    @NotBlank
    private String idEmployee;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
