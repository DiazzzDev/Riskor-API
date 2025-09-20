package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOMaintenanceControlSD {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idMaintenanceControlSD;

    @NotNull
    private LocalDate dateMaintenance;

    @NotBlank @Size(min = 5, max = 250, message = "La descripción debe contener entre 5 a 250 carácteres")
    private String description;

    @NotBlank @Size(min = 2, max = 120, message = "La entidad o persona encargada debe contener entre 2 a 125 carácteres")
    private String carriedOutBy;

    @Size(max = 250, message = "La observación debe contener entre 5 a 250 carácteres")
    private String observation;

    @NotBlank
    private String idServiceDeviceSSO;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
