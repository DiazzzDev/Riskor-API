package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOSecurityQuestion;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceSecurityQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/SecurityQ")
public class ControllerSecurityQuestion {
    //Inyectamos el Service
    @Autowired
    private ServiceSecurityQuestion objServiceSQ;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getSecurityQ")
    public List<DTOSecurityQuestion> getData(){
        return objServiceSQ.getAllSecurityQ();
    }
}
