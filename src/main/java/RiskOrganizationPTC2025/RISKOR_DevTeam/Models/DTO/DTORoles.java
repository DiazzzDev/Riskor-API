package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTORoles {
    private String idRole;
    @NotBlank @Size(max = 35, message = "Se han excedido la cantidad máxima de carácteres en el rol.")
    private String roleName;
}
