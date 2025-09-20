package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntitySecurityQuestion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOQuestionEmployee {

    private String idQuestionsEmployee;
    @NotBlank
    private String answerEmployee;
    @NotNull
    private EntityEmployee idEmployee;
    @NotNull
    private EntitySecurityQuestion idQuestion;
}
