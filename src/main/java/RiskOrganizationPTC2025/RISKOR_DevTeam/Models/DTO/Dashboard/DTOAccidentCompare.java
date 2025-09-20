package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAccidentCompare {
    private long thisMonth;
    private long lastMonth;
    public DTOAccidentCompare(long e, long a){ this.thisMonth =e; this.lastMonth=a; }
}
