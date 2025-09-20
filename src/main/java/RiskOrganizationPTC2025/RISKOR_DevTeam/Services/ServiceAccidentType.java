package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceAccidentType {
    @Autowired
    private RepositoryAccidentType objRepoAccidentType;

    @Transactional(readOnly = true)
    public List<DTOAccidentType> getAllAccidentTypes(){
        List<EntityAccidentType> accidentTypes = objRepoAccidentType.findAll();
        return accidentTypes.stream().map(this::convertToAccidentTypeDTO).collect(Collectors.toList());
    }

    public DTOAccidentType convertToAccidentTypeDTO(EntityAccidentType accidentType){
        DTOAccidentType accidentTypeDTO = new DTOAccidentType();
        accidentTypeDTO.setIdAccidentType(accidentType.getIdAccidentType());
        accidentTypeDTO.setAccidentType(accidentType.getAccidentType());
        return accidentTypeDTO;
    }
}
