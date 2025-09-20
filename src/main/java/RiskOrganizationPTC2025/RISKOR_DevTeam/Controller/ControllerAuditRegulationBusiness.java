package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAuditRegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAuditRegulationBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auditRegulationBusiness")
@Validated
public class ControllerAuditRegulationBusiness {
    @Autowired
    private ServiceAuditRegulationBusiness objServiceARB;

    @GetMapping("/getAuditRegulationBusiness")
    public List<DTOAuditRegulationBusiness> getAuditRegulationBusiness(@RequestAttribute("auth.business") String idBusiness){
        return objServiceARB.getARB(idBusiness);
    }
}
