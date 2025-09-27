package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentCategory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accidentCategory")
public class ControllerAccidentCategory {
    @Autowired
    private ServiceAccidentCategory objServiceAC;

    @GetMapping("/getAccidentCategories")
    public List<DTOAccidentCategory> getAccidentCategories(){
        return objServiceAC.getAllAccidentCategories();
    }
}
