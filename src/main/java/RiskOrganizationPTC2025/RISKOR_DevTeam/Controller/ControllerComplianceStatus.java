package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOComplianceStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceComplianceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complianceS")
public class ControllerComplianceStatus {
    //Inyectamos el Service
    @Autowired
    private ServiceComplianceStatus objServiceCS;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getComplianceS")
    public List<DTOComplianceStatus> getData(){
        return objServiceCS.getAllComplianceS();
    }
}
