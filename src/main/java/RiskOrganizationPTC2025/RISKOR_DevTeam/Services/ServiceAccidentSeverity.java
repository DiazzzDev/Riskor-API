package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentSeverity;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentSeverity;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceAccidentSeverity {
    @Autowired
    private RepositoryAccidentSeverity objRepoAS;

    @Transactional(readOnly = true)
    public List<DTOAccidentSeverity> getAllAccidentSeverities(){
        List<EntityAccidentSeverity> accidentSeverities = objRepoAS.findAll();
        return accidentSeverities.stream().map(this::convertToASDTO).collect(Collectors.toList());
    }

    public DTOAccidentSeverity convertToASDTO(EntityAccidentSeverity severity){
        DTOAccidentSeverity objASDTO = new DTOAccidentSeverity();
        objASDTO.setIdAccidentSeverity(severity.getIdAccidentSeverity());
        objASDTO.setAccidentSeverity(severity.getAccidentSeverity());
        return objASDTO;
    }
}
