package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DTOCalendarItem {
    private LocalDate trainingDate;
    private String title;
    public DTOCalendarItem(LocalDate d, String t){ this.trainingDate=d; this.title=t; }
}
