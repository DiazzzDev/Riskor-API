package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTORiskAreasExtremes {
    private DTORiskArea minRiskArea;
    private DTORiskArea mayorRiskArea;
    public DTORiskAreasExtremes(DTORiskArea min, DTORiskArea max){ this.minRiskArea =min; this.mayorRiskArea =max; }
}
