package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAccidentMonthCount {
    private String yyyymm;
    private long total;
    public DTOAccidentMonthCount(String ym, long t){ this.yyyymm=ym; this.total=t; }
}
