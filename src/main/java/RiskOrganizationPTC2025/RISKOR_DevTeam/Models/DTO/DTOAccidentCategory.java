package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAccidentCategory {
    private String idAccidentCategory;
    @NotBlank @Size(max = 50, message = "Se han excedido la cantidad máxima de carácteres en la categoría de accidente.")
    private String accidentCategory;
}
