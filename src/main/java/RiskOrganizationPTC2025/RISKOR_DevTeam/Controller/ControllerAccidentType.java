package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accidentType")
@Validated
public class ControllerAccidentType {
    @Autowired
    private ServiceAccidentType objServiceAT;

    @GetMapping("/getAccidentTypes")
    public List<DTOAccidentType> getAccidentTypes(){
        return objServiceAT.getAllAccidentTypes();
    }
}
