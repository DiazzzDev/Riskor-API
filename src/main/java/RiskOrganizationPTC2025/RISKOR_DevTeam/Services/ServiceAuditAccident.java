package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAuditAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAuditAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAuditAccident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ServiceAuditAccident {
    @Autowired
    private RepositoryAuditAccident objRepoAA;

    //Método GET principal de auditoría, lista para solo pedir como filtrar la tabla
    @Transactional(readOnly = true)
    public Page<DTOAuditAccident> search(
            String idBusiness, String operationType,
            String username, String accidentId,
            LocalDate fromDate, LocalDate toDate,
            int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "operationDate"));
        Page<EntityAuditAccident> p = objRepoAA.search(idBusiness.toUpperCase(), operationType, username, accidentId, fromDate, toDate, pageable);
        return p.map(this::convertToDTOAA);
    }

    private DTOAuditAccident convertToDTOAA(EntityAuditAccident auditAccident){
        DTOAuditAccident dtoAA = new DTOAuditAccident();
        dtoAA.setIdAuditAccident(auditAccident.getIdAuditAccident());
        dtoAA.setOperationType(auditAccident.getOperationType());
        dtoAA.setOperationDate(auditAccident.getOperationDate());
        dtoAA.setUsername(auditAccident.getUsername());
        dtoAA.setIdAccident(auditAccident.getIdAccident());
        dtoAA.setDescription(auditAccident.getDescription());
        dtoAA.setAccidentDate(auditAccident.getAccidentDate());
        dtoAA.setReportAccident(auditAccident.getReportAccident());
        dtoAA.setIdAccidentCategory(auditAccident.getIdAccidentCategory());
        dtoAA.setIdAccidentType(auditAccident.getIdAccidentType());
        dtoAA.setIdAccidentSeverity(auditAccident.getIdAccidentSeverity());
        dtoAA.setIdAccidentStatus(auditAccident.getIdAccidentStatus());
        dtoAA.setIdEmployee(auditAccident.getIdEmployee());
        dtoAA.setIdLocation(auditAccident.getIdLocation());
        return dtoAA;
    }
}
