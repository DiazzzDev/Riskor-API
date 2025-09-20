package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceAccidentStatus {
    @Autowired
    private RepositoryAccidentStatus objRepoAS;

    @Transactional(readOnly = true)
    public List<DTOAccidentStatus> getAllAccidentStatus(){
        List<EntityAccidentStatus> status = objRepoAS.findAll();
        return status.stream().map(this::convertToAccidentStatusDTO).collect(Collectors.toList());
    }

    public DTOAccidentStatus convertToAccidentStatusDTO(EntityAccidentStatus status) {
        DTOAccidentStatus objDTOAS = new DTOAccidentStatus();
        objDTOAS.setIdAccidentStatus(status.getIdAccidentStatus());
        objDTOAS.setAccidentStatus(status.getAccidentStatus());
        return objDTOAS;
    }
}
