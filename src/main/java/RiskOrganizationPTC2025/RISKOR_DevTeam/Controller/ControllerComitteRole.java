package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOComitteRole;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceComitteRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/committeeRoles")
public class ControllerComitteRole {
    @Autowired
    private ServiceComitteRole objServiceCommitteR;

    @GetMapping("/getCommitteeRoles")
    public List<DTOComitteRole> getCommitteeRoles(){
        return objServiceCommitteR.getAllComitteRoles();
    }
}
