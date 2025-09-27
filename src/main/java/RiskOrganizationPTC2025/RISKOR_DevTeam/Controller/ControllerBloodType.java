package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBloodType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceBloodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bloodT")
public class ControllerBloodType {
    //Inyectamos el Service
    @Autowired
    private ServiceBloodType objServiceBT;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getBloodT")
    public List<DTOBloodType> getData(){
        return objServiceBT.getAllBloodType();
    }
}
