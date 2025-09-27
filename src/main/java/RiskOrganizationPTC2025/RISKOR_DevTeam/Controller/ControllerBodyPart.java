package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceBodyPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/bodyParts")
public class ControllerBodyPart {
    @Autowired
    private ServiceBodyPart objServiceBP;

    @GetMapping("/getBodyParts")
    public List<DTOBodyPart> getBodyParts(){
        return objServiceBP.getAllBodyParts();
    }
}
