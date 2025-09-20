package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypePermission;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTypePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/typeP")
@Validated
public class ControllerTypePermission {
    //Inyectamos el Service
    @Autowired
    private ServiceTypePermission objServiceTP;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getTypeP")
    public List<DTOTypePermission> getData(){
        return objServiceTP.getAllTypePermission();
    }
}
