package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMedicalStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceMedicalStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicalS")
public class ControllerMedicalStatus {
    //Inyectamos el Service
    @Autowired
    private ServiceMedicalStatus objServiceMS;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getMedicalS")
    public List<DTOMedicalStatus> getData(){
        return objServiceMS.getAllMedicalStatus();
    }
}
