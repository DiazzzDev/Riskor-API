package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTORatingItem {
    private String idTrainingEmployee;
    private int ratingTraining;
    public DTORatingItem(String id, int r){ this.idTrainingEmployee=id; this.ratingTraining=r; }
}
