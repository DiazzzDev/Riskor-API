package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOControlSDStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceControlSDStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/controlSDStatus")
@Validated
public class ControllerControlSDStatus {
    //Inyectamos el Service
    @Autowired
    private ServiceControlSDStatus objServiceCSDS;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getControlSDStatus")
    public List<DTOControlSDStatus> getData(){
        return objServiceCSDS.getAllControlSDStatus();
    }
}
