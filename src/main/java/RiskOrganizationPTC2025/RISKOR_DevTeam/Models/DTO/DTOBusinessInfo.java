package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("NIT")
    @JsonAlias({"nit","Nit"})
    @NotBlank(message = "El NIT no puede estar vacío.")
    @Pattern(regexp = "^\\d{4}-?\\d{6}-?\\d{3}-?\\d{1}$",
            message = "El NIT debe tener 14 dígitos en el formato XXXX-XXXXXX-XXX-X o 14 dígitos continuos.")
    private String NIT;
}
