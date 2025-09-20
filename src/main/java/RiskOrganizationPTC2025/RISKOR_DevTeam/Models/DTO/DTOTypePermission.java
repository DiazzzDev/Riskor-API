package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOTypePermission {
    private String idTypePermission;
    @NotBlank @Size(max = 50, message = "Se han excedido la cantidad máxima de carácteres en los tipos de permisos.")
    private String permissionType;
}
