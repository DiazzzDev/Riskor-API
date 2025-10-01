package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import java.time.LocalDate;

@Getter @Setter
public class DTOInspection {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idInspection;

    @NotBlank @Size(min = 5, max = 75, message = "El título debe contener de 5 a 75 carácteres")
    private String inspectionTitle;

    @Nullable
    @Size(max = 1000, message = "Evidencia es requerida para la inspección")
    private String inspectionEvidence;

    private LocalDate inspectionDate;

    @NotBlank @Size(min = 10, max = 250, message = "El título debe contener de 10 a 250 carácteres")
    private String observation;

    @NotNull(message = "El empleado que realizó la inspección es obligatorio")
    private String idEmployee;

    private String firstName;

    private String lastName;

    @NotNull(message = "El área es obligatorio")
    private String idArea;

    @NotBlank(message = "El tipo de inspección es obligatorio")
    private String idInspectionType;

    private String inspectionType;

    @NotBlank(message = "El estatus de inspección es obligatorio")
    private String idInspectionStatus;

    private String inspectionStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
