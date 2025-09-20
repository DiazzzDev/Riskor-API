package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class DTOTraining {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idTraining;

    @NotBlank @Size(min = 3, max = 100)
    private String title;

    @NotBlank @Size(min = 10, max = 255)
    private String description;

    @NotBlank @Size(min = 10, max = 125)
    private String fullNameTraining;

    private LocalDate requestDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "La fecha de la capacitación debe ser en el futuro.")
    private LocalDate trainingDate;

    @NotBlank
    private String startHour;

    @NotBlank
    private String endHour;

    private String durationHour;

    @NotBlank
    @Size(min = 4, max = 100)
    private String trainingLocation;

    @NotBlank
    private String idTrainingModality;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}