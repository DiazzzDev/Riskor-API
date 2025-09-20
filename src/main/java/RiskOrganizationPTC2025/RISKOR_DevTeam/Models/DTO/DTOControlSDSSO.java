package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOControlSDSSO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idServiceDeviceSSO;

    @NotBlank
    @Size(min = 2, max = 100, message = "El nombre del servicio debe contener de 2 a 100 carácteres")
    private String nameServiceDevice;

    @Size(max = 255, message = "La descripción solo puede contener 255 carácteres")
    private String description;

    @NotNull
    private LocalDate installationDate;

    private LocalDate maintenanceDate;

    @NotBlank
    private String idEmployee;

    @NotBlank
    private String idTypeControlSD;

    @NotBlank
    private String idLocation;

    @NotBlank
    private String idControlSDStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
