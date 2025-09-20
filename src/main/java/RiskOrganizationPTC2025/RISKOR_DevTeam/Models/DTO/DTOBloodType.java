package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOBloodType {
    private String idBloodType;
    @NotBlank @Size(max = 3, message = "Se han excedido la cantidad máxima de carácteres en los tipos de sangre.")
    private String bloodType;
}
