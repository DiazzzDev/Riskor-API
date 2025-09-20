package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingModality;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingModality;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingModality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceTrainingModality {
    @Autowired
    private RepositoryTrainingModality objRepoTrainingModality;

    @Transactional(readOnly = true)
    public List<DTOTrainingModality> getAllTModalities(){
        List<EntityTrainingModality> modality = objRepoTrainingModality.findAll();
        return modality.stream().map(this::convertToTModalityDTO).collect(Collectors.toList());
    }

    public DTOTrainingModality convertToTModalityDTO(EntityTrainingModality trainingModality){
        DTOTrainingModality objDTOTModality = new DTOTrainingModality();
        objDTOTModality.setIdTrainingModality(trainingModality.getIdTrainingModality());
        objDTOTModality.setTrainingModality(trainingModality.getTrainingModality());
        return objDTOTModality;
    }
}
