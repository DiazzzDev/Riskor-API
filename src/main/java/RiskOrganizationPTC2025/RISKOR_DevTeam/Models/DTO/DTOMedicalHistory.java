package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOMedicalHistory {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idMedicalHistory;

    @NotBlank
    private String medicalCondition;

    @NotNull
    private LocalDate diagnosisDate;

    @NotBlank
    private String treatment;

    @NotNull
    private LocalDate treatmentStartDate;

    @NotNull
    private LocalDate treatmentEndDate;

    // IDs relacionales como String (el Service hace getReference)
    @NotBlank(message = "Debe indicar el estado médico")
    private String idMedicalStatus;

    @NotBlank(message = "Debe indicar el expediente médico")
    private String idMedicalRecord;

    //Seguridad: lo setea el backend desde el path, NO DESDE EL JSON
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}
