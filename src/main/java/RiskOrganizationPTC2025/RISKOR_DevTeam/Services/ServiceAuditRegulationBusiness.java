package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAuditRegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAuditRegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAuditRegulationBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceAuditRegulationBusiness {
    @Autowired
    private RepositoryAuditRegulationBusiness objRepoARB;

    @Transactional(readOnly = true)
    public List<DTOAuditRegulationBusiness> getARB(String idBusiness){
        List<EntityAuditRegulationBusiness> auditRegulationBusinesses = objRepoARB.findByIdBusiness(idBusiness);
        return auditRegulationBusinesses.stream().map(this::convertToDTOARB).collect(Collectors.toList());
    }

    private DTOAuditRegulationBusiness convertToDTOARB(EntityAuditRegulationBusiness auditRegulationBusiness){
        DTOAuditRegulationBusiness dtoARB = new DTOAuditRegulationBusiness();
        dtoARB.setIdAuditRB(auditRegulationBusiness.getIdAuditRB());
        dtoARB.setOperationType(auditRegulationBusiness.getOperationType());
        dtoARB.setOperationDate(auditRegulationBusiness.getOperationDate());
        dtoARB.setUsername(auditRegulationBusiness.getUsername());
        dtoARB.setIdRegulation(auditRegulationBusiness.getIdRegulation());
        dtoARB.setRegulationTitle(auditRegulationBusiness.getRegulationTitle());
        dtoARB.setRegulationDescription(auditRegulationBusiness.getRegulationDescription());
        dtoARB.setCreationDate(auditRegulationBusiness.getCreationDate());
        dtoARB.setIdRiskStatus(auditRegulationBusiness.getIdRiskStatus());
        dtoARB.setIdRegulationCategory(auditRegulationBusiness.getIdRegulationCategory());
        dtoARB.setIdArea(auditRegulationBusiness.getIdArea());
        dtoARB.setIdRiskLevel(auditRegulationBusiness.getIdRiskLevel());
        dtoARB.setIdBusiness(auditRegulationBusiness.getIdBusiness());
        return dtoARB;
    }
}
