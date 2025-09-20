package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Getter @Setter
public class DTOAuditRegulationBusiness {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID Será ignorado
    private String idAuditRB;

    @Size(max = 10)
    private String operationType;
    private LocalDate operationDate;

    @Size(max = 30)
    private String username;

    @Nullable
    private String idRegulation;

    @Size(max = 255)
    @Nullable
    private String regulationTitle;

    @Size(max = 1000)
    @Nullable
    private String regulationDescription;

    @Nullable
    private LocalDate creationDate;

    @Nullable
    private String idRiskStatus;

    @Nullable
    private String idRegulationCategory;

    @Nullable
    private String idArea;

    @Nullable
    private String idRiskLevel;

    @Nullable
    private String idBusiness;
}
