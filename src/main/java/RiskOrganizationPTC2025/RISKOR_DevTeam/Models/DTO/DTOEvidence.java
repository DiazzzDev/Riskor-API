package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter @Setter
public class DTOEvidence {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idEvidence;

    @Nullable
    private String accidentEvidence;

    @NotBlank(message = "idAccident es obligatorio")
    private String idAccident;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;
}
