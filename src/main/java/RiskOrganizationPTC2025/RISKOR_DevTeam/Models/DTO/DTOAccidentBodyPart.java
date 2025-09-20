package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBodyPart;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAccidentBodyPart {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idAccidentBodyPart;

    @NotBlank(message = "idAccident es obligatorio")
    private String idAccident;

    @NotBlank(message = "idBodyPart es obligatorio")
    private String idBodyPart;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}