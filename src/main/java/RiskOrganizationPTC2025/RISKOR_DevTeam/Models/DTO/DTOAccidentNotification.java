package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOAccidentNotification {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID en POST Será ignorado
    private String idAccNotification;

    @Size(max = 250)
    private String message;
    private LocalDate notificationDate;
    private LocalDate expirationDate;

    @Size(max = 1)
    private String isDeleted;
    private LocalDate creationDate;
    private String idEmployee;
    private String idAccident;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras
    private String idBusiness;
}
