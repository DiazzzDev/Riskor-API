package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOUser {
    @Size(min = 6, max = 30, message = "El nombre de usuario debe tener de 6 a 30 carácteres")
    private String username;

    @NotBlank
    @Size(min = 8, max = 256, message = "La contraseña debe tener un mínimo de 8 carácteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //Con esto la contraseña no es devuelta ni hasheada en el JSON
    private String password;

    private String status;
    private LocalDate creationDate;
}
