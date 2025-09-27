package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOComittePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceComittePosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/committeePositions")
public class ControllerComittePosition {
    @Autowired
    private ServiceComittePosition objServiceCommittePosition;

    @GetMapping("/getCommitteePositions")
    public List<DTOComittePosition> getCommittePositions(){
        return objServiceCommittePosition.getAllComitteRoles();
    }
}
