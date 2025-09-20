package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accidentStatus")
@Validated
public class ControllerAccidentStatus {
    @Autowired
    private ServiceAccidentStatus objServiceAS;

    @GetMapping("/getAccidentStatus")
    public List<DTOAccidentStatus> getAccidentStatus(){
        return objServiceAS.getAllAccidentStatus();
    }
}
