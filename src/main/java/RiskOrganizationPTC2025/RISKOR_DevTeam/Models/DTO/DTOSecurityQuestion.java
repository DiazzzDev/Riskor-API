package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOSecurityQuestion {
    private String idQuestion;
    @NotBlank @Size(max = 125, message = "Se han excedido la cantidad máxima de carácteres en la pregunta de seguridad.")
    private String nameQuestion;
}
