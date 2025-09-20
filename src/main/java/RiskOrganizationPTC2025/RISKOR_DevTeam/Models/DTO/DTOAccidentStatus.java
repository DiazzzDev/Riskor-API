package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

@Getter @Setter
public class DTOAccidentStatus {
    private String idAccidentStatus;
    @NotBlank @Size(max = 50, message = "Se han excedido la cantidad máxima de carácteres en el status de los accidentes.")
    private String accidentStatus;
}
