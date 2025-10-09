package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingModality;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTrainingModality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/trainingModality")
public class ControllerTrainingModality {
    @Autowired
    private ServiceTrainingModality objServiceTM;

    @GetMapping("/getAllTrainingModalities")
    public List<DTOTrainingModality> getAllTM(){
        return objServiceTM.getAllTModalities();
    }
}
