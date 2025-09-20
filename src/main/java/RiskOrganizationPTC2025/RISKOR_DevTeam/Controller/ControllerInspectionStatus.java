package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceInspectionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inspectionS")
@Validated
public class ControllerInspectionStatus {
    //Inyectamos el Service
    @Autowired
    private ServiceInspectionStatus objServiceIS;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getInspectionS")
    public List<DTOInspectionStatus> getData(){
        return objServiceIS.getAllInspectionS();
    }
}
