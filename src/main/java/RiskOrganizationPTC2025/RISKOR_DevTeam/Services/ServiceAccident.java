package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccident;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
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
public class ServiceAccident {
    @Autowired
    private RepositoryAccident objRepoA;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public DTOAccident getById(String idAccident, String idBusiness) {
        EntityAccident accident = objRepoA.findByIdAccidentAndIdBusiness_IdBusiness(idAccident, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Accidente no encontrado"));
        return convertToDTOA(accident);
    }

    @Transactional(readOnly = true)
    public Page<DTOAccident> findAll(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "accidentDate"));
        Page<EntityAccident> accidents = objRepoA.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return accidents.map(this::convertToDTOA);
    }

    @Transactional(readOnly = true)
    public Page<DTOAccident> search(
            String idBusiness, String employeeId, String statusId,
            LocalDate fromDate, LocalDate toDate, String employeeInfo, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "accidentDate"));
        Page<EntityAccident> pageE = objRepoA.search(idBusiness.toUpperCase(), employeeId, statusId, fromDate, toDate, employeeInfo, pageable);
        return pageE.map(this::convertToDTOA);
    }

    public DTOAccident postAccident(@Valid DTOAccident dtoA, String idBusiness, String emailEmployee) {
        if (dtoA == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Validación - consistencia entre fechas
        LocalDate report = dtoA.getReportAccident() != null ? dtoA.getReportAccident() : LocalDate.now();
        if (dtoA.getAccidentDate() != null && report.isBefore(dtoA.getAccidentDate())) {
            throw new IllegalArgumentException("La fecha que se reportó el accidente no puede ser anterior a la fecha del accidente");
        }

        EntityAccident accident = new EntityAccident();
        accident.setDescription(dtoA.getDescription());
        accident.setAccidentDate(dtoA.getAccidentDate());
        accident.setReportAccident(report);

        //if (dto.getIdAccidentCategory() != null) e.setIdAccidentCategory(em.getReference(EntityAccidentCategory.class, dto.getIdAccidentCategory()));
        //if (dto.getIdAccidentType() != null) e.setIdAccidentType(em.getReference(EntityAccidentType.class, dto.getIdAccidentType()));
        //if (dto.getIdAccidentSeverity() != null) e.setIdAccidentSeverity(em.getReference(EntityAccidentSeverity.class, dto.getIdAccidentSeverity()));
        if (dtoA.getIdAccidentStatus() != null) accident.setIdAccidentStatus(em.getReference(EntityAccidentStatus.class, dtoA.getIdAccidentStatus()));

        accident.setIdEmployee(em.getReference(EntityEmployee.class, dtoA.getIdEmployee()));
        accident.setIdLocation(em.getReference(EntityLocation.class, dtoA.getIdLocation()));
        accident.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));
        accident.setSentBy(emailEmployee);

        EntityAccident saved = objRepoA.save(accident);
        return convertToDTOA(saved);
    }

    public DTOAccident putAccident(@Valid DTOAccident dtoA, String idAccident, String idBusiness) {
        EntityAccident accident = objRepoA.findByIdAccidentAndIdBusiness_IdBusiness(idAccident, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Accidente no encontrado"));

        //Si no se cambiaron los nuevos valores en el DTO los va a dejar como eran originalmente, sino los actualiza
        if (dtoA.getDescription() != null) accident.setDescription(dtoA.getDescription());
        if (dtoA.getAccidentDate() != null) accident.setAccidentDate(dtoA.getAccidentDate());

        //Actualizar valor + validación - No se permite que la fecha que se reportó el accidente suceda antes que el accidente
        if (dtoA.getReportAccident() != null) {
            if (accident.getAccidentDate() != null && dtoA.getReportAccident().isBefore(accident.getAccidentDate())) {
                throw new IllegalArgumentException("La fecha que se reportó el accidente no puede ser anterior a la fecha del accidente");
            }
            accident.setReportAccident(dtoA.getReportAccident());
        }

        //FK que pueden ser NULL
        if (dtoA.getIdAccidentCategory() != null) accident.setIdAccidentCategory(em.getReference(EntityAccidentCategory.class, dtoA.getIdAccidentCategory()));
        if (dtoA.getIdAccidentType() != null) accident.setIdAccidentType(em.getReference(EntityAccidentType.class, dtoA.getIdAccidentType()));
        if (dtoA.getIdAccidentSeverity() != null) accident.setIdAccidentSeverity(em.getReference(EntityAccidentSeverity.class, dtoA.getIdAccidentSeverity()));
        if (dtoA.getIdAccidentStatus() != null) accident.setIdAccidentStatus(em.getReference(EntityAccidentStatus.class, dtoA.getIdAccidentStatus()));

        //FKs NOT NULL
        if (dtoA.getIdEmployee() != null) accident.setIdEmployee(em.getReference(EntityEmployee.class, dtoA.getIdEmployee()));
        if (dtoA.getIdLocation() != null) accident.setIdLocation(em.getReference(EntityLocation.class, dtoA.getIdLocation()));

        return convertToDTOA(accident); //JPA sincroniza por @Transactional
    }

    public boolean removeAccident(String idAccident, String idBusiness) {
        long rows = objRepoA.deleteByIdAccidentAndIdBusiness_IdBusiness(idAccident, idBusiness.toUpperCase());
        if (rows == 0) throw new EntityNotFoundException("Accidente no encontrado");
        return true;
    }

    private DTOAccident convertToDTOA(EntityAccident accident){
        DTOAccident dtoA = new DTOAccident();
        dtoA.setIdAccident(accident.getIdAccident());
        dtoA.setDescription(accident.getDescription());
        dtoA.setAccidentDate(accident.getAccidentDate());
        dtoA.setReportAccident(accident.getReportAccident());
        dtoA.setIdAccidentCategory(accident.getIdAccidentCategory() != null ? accident.getIdAccidentCategory().getIdAccidentCategory() : null);
        dtoA.setIdAccidentType(accident.getIdAccidentType() != null ? accident.getIdAccidentType().getIdAccidentType() : null);
        dtoA.setIdAccidentSeverity(accident.getIdAccidentSeverity() != null ? accident.getIdAccidentSeverity().getIdAccidentSeverity() : null);
        dtoA.setIdAccidentStatus(accident.getIdAccidentStatus() != null ? accident.getIdAccidentStatus().getIdAccidentStatus() : null);
        dtoA.setIdEmployee(accident.getIdEmployee() != null ? accident.getIdEmployee().getIdEmployee() : null);
        dtoA.setIdLocation(accident.getIdLocation() != null ? accident.getIdLocation().getIdLocation() : null);
        dtoA.setIdBusiness(accident.getIdBusiness() != null ? accident.getIdBusiness().getIdBusiness() : null);

        return dtoA;
    }
}
