package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTORegulationBusiness {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID Será ignorado
    private String idRegulation;

    @NotBlank
    @Size(min = 6, max = 255, message = "La regulación debe tener de 6 a 255 carácteres")
    private String regulationTitle;

    @NotBlank
    @Size(min = 20, max = 825, message = "La descripción debe tener de 20 a 825 carácteres")
    private String regulationDescription; //No se usa regex por que pueden mencionase listas 1)

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate creationDate;

    private String regulationDocument;

    @NotBlank(message = "idRiskStatus es obligatorio")
    private String idRiskStatus;

    @NotBlank(message = "idRegulationCategory es obligatorio")
    private String idRegulationCategory;

    @NotBlank(message = "idArea es obligatorio")
    private String idArea;

    @NotBlank(message = "idRiskLevel es obligatorio")
    private String idRiskLevel;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}
