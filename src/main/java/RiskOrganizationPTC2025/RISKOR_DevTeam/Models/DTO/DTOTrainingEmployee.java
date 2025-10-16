package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import java.time.LocalDate;

@Getter @Setter
public class DTOTrainingEmployee {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idTrainingEmployee;

    @Nullable
    @Size(max = 1)
    @Pattern(regexp = "^[SN]{1}$", message = "Attendance debe ser 'S' o 'N'") //Solo Permite 'S' y 'N' como exige la db
    private String attendance;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @Nullable
    @Size(max = 255)
    private String observation;

    @NotBlank
    private String idEmployee;

    private String photo;
    private String firstname;
    private String lastname;
    private String DUI;

    @NotBlank
    private String idTraining;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}