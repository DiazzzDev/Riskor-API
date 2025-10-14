package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Getter @Setter
public class DTOAccident {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idAccident;

    @NotBlank
    @Size(max = 250)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accidentDate;

    //Si no viene, el service fija hoy (DB tiene DEFAULT SYSDATE, pero JPA manda null si lo dejas vacío)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportAccident;

    @Nullable
    private String idAccidentCategory;

    private String accidentCategory;

    @Nullable
    private String idAccidentType;

    private String accidentType;

    @Nullable
    private String idAccidentSeverity;

    private String accidentSeverity;

    @Nullable
    private String idAccidentStatus;

    private String accidentStatus;

    @NotBlank
    private String idEmployee;

    private String employee;

    @NotBlank
    private String idLocation;

    private String location;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Para no permitir que el cliente cambie empresa por body; la inyectas desde el path.
    private String idBusiness;

    private String business;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sentBy;
}
