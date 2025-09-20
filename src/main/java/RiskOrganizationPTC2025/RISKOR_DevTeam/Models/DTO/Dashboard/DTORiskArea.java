package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTORiskArea {
    private String idLocation, locationName;
    private long total;
    public DTORiskArea(String id, String name, long t){ this.idLocation=id; this.locationName=name; this.total=t; }
}
