package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceInspectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inspectionT")
public class ControllerInspectionType {
    //Inyectamos el Service
    @Autowired
    private ServiceInspectionType objServiceIT;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getInspectionT")
    public List<DTOInspectionType> getData(){
        return objServiceIT.getAllInspectionT();
    }
}
