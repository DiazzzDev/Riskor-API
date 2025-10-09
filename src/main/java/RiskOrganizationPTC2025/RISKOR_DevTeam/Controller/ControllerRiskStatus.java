package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORiskStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceRiskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/riskStatus")
public class ControllerRiskStatus {
    @Autowired
    private ServiceRiskStatus objServiceRS;

    @GetMapping("/getRiskStatus")
    public List<DTORiskStatus> getRiskStatus(){
        return objServiceRS.getAllRiskStatus();
    }
}
