package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRiskStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORiskStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryRiskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRiskStatus {
    @Autowired
    private RepositoryRiskStatus objRepoRS;

    @Transactional(readOnly = true)
    public List<DTORiskStatus> getAllRiskStatus(){
        List<EntityRiskStatus> status = objRepoRS.findAll();
        return status.stream().map(this::convertToRSDTO).collect(Collectors.toList());
    }

    public DTORiskStatus convertToRSDTO(EntityRiskStatus status){
        DTORiskStatus objDTORS = new DTORiskStatus();
        objDTORS.setIdRiskStatus(status.getIdRiskStatus());
        objDTORS.setRiskStatus(status.getRiskStatus());
        return objDTORS;
    }
}
