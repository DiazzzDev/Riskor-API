package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORoles;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class ControllerRoles {
    @Autowired
    private ServiceRoles objServiceR;

    @GetMapping("/getRoles")
    public List<DTORoles> getRoles(){
        return objServiceR.getAllRoles();
    }
}
