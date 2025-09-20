package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRiskLevel;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORiskLevel;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryRiskLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRiskLevel {
    @Autowired
    private RepositoryRiskLevel objRepoRL;

    @Transactional(readOnly = true)
    public List<DTORiskLevel> getAllRiskLevels(){
        List<EntityRiskLevel> levels = objRepoRL.findAll();
        return levels.stream().map(this::convertToDTORiskLevel).collect(Collectors.toList());
    }

    public DTORiskLevel convertToDTORiskLevel(EntityRiskLevel level){
        DTORiskLevel objDTORL = new DTORiskLevel();
        objDTORL.setIdRiskLevel(level.getIdRiskLevel());
        objDTORL.setRiskLevelName(level.getRiskLevelName());
        return objDTORL;
    }
}
