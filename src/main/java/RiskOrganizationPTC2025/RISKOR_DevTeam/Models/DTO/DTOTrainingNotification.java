package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTraining;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOTrainingNotification {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idTrnNotification;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate notificationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    private String isDeleted; //'Y'|'N'

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private String idEmployee;
    private String idTraining;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}