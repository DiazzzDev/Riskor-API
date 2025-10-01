package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class DTOTraining {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idTraining;

    @NotBlank @Size(min = 3, max = 100, message = "El título debe contener de 3 a 100 carácteres")
    private String title;

    @NotBlank @Size(min = 10, max = 255, message = "La descripción debe contener de 10 a 255 carácteres")
    private String description;

    @NotBlank @Size(min = 10, max = 125)
    private String fullNameTraining;

    private LocalDate requestDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "La fecha de la capacitación debe ser el día actual o en el futuro.")
    private LocalDate trainingDate;

    @NotBlank
    private String startHour;

    @NotBlank
    private String endHour;

    private String durationHour;

    @NotBlank
    @Size(min = 4, max = 100, message = "La ubicación de la capacitación debe contener de 4 a 100 carácteres")
    private String trainingLocation;

    @NotBlank
    private String idTrainingModality;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}