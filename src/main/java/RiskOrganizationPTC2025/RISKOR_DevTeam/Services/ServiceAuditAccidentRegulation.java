package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAuditAccidentRegulation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAuditAccidentRegulation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAuditAccidentRegulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceAuditAccidentRegulation {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryAuditAccidentRegulation objRepoAAR;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOAuditAccidentRegulation> getAllAuditAR(){
        List<EntityAuditAccidentRegulation> objGetAuditAccidentR = objRepoAAR.findAll();
        return objGetAuditAccidentR.stream().map(this::convertTOAuditAccidentRDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOAuditAccidentRegulation convertTOAuditAccidentRDTO(EntityAuditAccidentRegulation auditAccidentR){
        DTOAuditAccidentRegulation objAuditAccidentRDTO = new DTOAuditAccidentRegulation();
        objAuditAccidentRDTO.setIdAudit(auditAccidentR.getIdAudit());
        objAuditAccidentRDTO.setOperationType(auditAccidentR.getOperationType());
        objAuditAccidentRDTO.setOperationDate(auditAccidentR.getOperationDate());
        objAuditAccidentRDTO.setUsername(auditAccidentR.getUsername());
        objAuditAccidentRDTO.setIdAccidentRegulation(auditAccidentR.getIdAccidentRegulation());
        objAuditAccidentRDTO.setIdAccident(auditAccidentR.getIdAccident());
        objAuditAccidentRDTO.setIdRegulation(auditAccidentR.getIdRegulation());
        return objAuditAccidentRDTO;
    }
}
