package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORegulationCategory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceRegulationCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regulationCategory")
public class ControllerRegulationCategory {
    @Autowired
    private ServiceRegulationCategory objServiceRC;

    @GetMapping("/getRegulationCategories")
    public List<DTORegulationCategory> getRegulationCategories(){
        return objServiceRC.getAllRegulationCategories();
    }
}
