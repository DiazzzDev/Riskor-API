package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOAuditAccidentRegulation {

    @NotBlank
    private String idAudit;
    @NotBlank
    private String operationType;
    @NotNull
    private LocalDate operationDate;
    @NotBlank
    private String username;
    private String idAccidentRegulation;
    private String idAccident;
    private String idRegulation;
}
