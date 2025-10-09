package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORiskLevel;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceRiskLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/riskLevel")
public class ControllerRiskLevel {
    @Autowired
    private ServiceRiskLevel objService;

    @GetMapping("/getRiskLevel")
    public List<DTORiskLevel> getRiskLevels(){
        return objService.getAllRiskLevels();
    }
}
