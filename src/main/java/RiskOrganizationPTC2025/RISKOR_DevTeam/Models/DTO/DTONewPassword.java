package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTONewPassword {
    @NotBlank(message = "La contraseña actual es obligatoria")
    @Size(min = 8, max = 256, message = "La contraseña actual debe tener un mínimo de 8 carácteres")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 256, message = "La nueva contraseña debe tener un mínimo de 8 carácteres")
    private String newPassword;

    @NotBlank(message = "La confirmación de la nueva contraseña es obligatoria")
    private String confirmNewPassword;
}