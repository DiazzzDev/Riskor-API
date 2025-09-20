package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOAttendanceSummary {
    private long attended;
    private long missed;
    public DTOAttendanceSummary(long a, long f){ this.attended =a; this.missed =f; }
}
