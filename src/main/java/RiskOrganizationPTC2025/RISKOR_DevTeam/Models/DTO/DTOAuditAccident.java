package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOAuditAccident {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID en POST Será ignorado
    private String idAuditAccident;

    @Size(max = 10)
    private String operationType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate operationDate;

    @Size(max = 50)
    private String username;
    private String idAccident;

    @Size(max = 250)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accidentDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportAccident;
    private String idAccidentCategory;
    private String idAccidentType;
    private String idAccidentSeverity;
    private String idAccidentStatus;
    private String idEmployee;
    private String idLocation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
