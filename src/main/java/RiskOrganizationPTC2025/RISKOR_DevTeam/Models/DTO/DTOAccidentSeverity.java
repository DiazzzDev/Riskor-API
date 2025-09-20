package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAccidentSeverity {
    private String idAccidentSeverity;
    @NotBlank @Size(max = 50, message = "Se han excedido la cantidad máxima de carácteres en la severidad de los accidentes.")
    private String accidentSeverity;
}
