package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeCategoryControlSD;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTypeCategoryControlSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/typeCategoryCSD")
public class ControllerTypeCategoryControlSD {
    //Inyectamos el Service
    @Autowired
    private ServiceTypeCategoryControlSD objServiceTCCSD;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getTypeCategoryCSD")
    public List<DTOTypeCategoryControlSD> getData(){
        return objServiceTCCSD.getAllTypeCategoryCSD();
    }
}
