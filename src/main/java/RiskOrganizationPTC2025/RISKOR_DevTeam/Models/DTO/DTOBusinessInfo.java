package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOBusinessInfo {
    private String idBusiness;
    @NotBlank
    @Size(min = 3, max = 125, message = "El nombre del negocio debe tener de 3 a 125 carácteres")
    private String nameBusiness;

    @NotBlank
    @Size(min = 5, max = 250, message = "La dirección debe tener de 5 a 250 carácteres")
    private String addressBusiness;

    @Email @NotBlank
    @Size(min = 5, max = 256, message = "El correo del negocio debe tener de 5 a 256 carácteres")
    private String emailBusiness;

    @NotNull
    private LocalDate creationDate; //Se da uso a LocalDate en lugar de Date por ser más moderno y seguro, más apropiado para el uso de API

    @NotBlank
    @Size(min = 6, max = 15, message = "El teléfono del negocio debe tener de 6 a 15 carácteres")

    private String phoneBusiness;

    @NotBlank
    @Size(min = 6, max = 15, message = "El PBX del negocio debe tener de 6 a 15 carácteres")
    private String pbxBusiness;
}
