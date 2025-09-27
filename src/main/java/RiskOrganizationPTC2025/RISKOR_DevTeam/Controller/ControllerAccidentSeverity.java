package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentSeverity;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accidentSeverity")
public class ControllerAccidentSeverity {
    @Autowired
    private ServiceAccidentSeverity objServiceAS;

    @GetMapping("/getAccidentSeverities")
    public List<DTOAccidentSeverity> getAccidentSeverities(){
        return objServiceAS.getAllAccidentSeverities();
    }
}
