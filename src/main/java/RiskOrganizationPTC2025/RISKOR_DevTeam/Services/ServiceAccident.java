package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec.AccidentSpecs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ServiceAccident {
    @Autowired
    private RepositoryAccident objRepoA;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ServiceEmailSender serviceEmailSender;

    @Autowired
    private RepositoryEmployee objRepoE;

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
            LocalDate fromDate, LocalDate toDate,
            String employeeInfo,     //Búsqueda por NOMBRE, DUI O CORREO
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.desc("accidentDate"),
                        Sort.Order.asc("idEmployee.lastName"),
                        Sort.Order.asc("idEmployee.firstName")
                )
        );

        Specification<EntityAccident> spec =
                AccidentSpecs.scope(idBusiness)
                        .and(AccidentSpecs.byEmployeeId(employeeId))
                        .and(AccidentSpecs.byStatus(statusId))
                        .and(AccidentSpecs.inDateRange(fromDate, toDate))
                        .and(AccidentSpecs.searchQ(employeeInfo));

        Page<EntityAccident> pageE = objRepoA.findAll(spec, pageable);
        return pageE.map(this::convertToDTOA);
    }

    public DTOAccident postAccident(@Valid DTOAccident dtoA, String idBusiness, String idEmployee) {
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

        if (dtoA.getIdAccidentStatus() != null) accident.setIdAccidentStatus(em.getReference(EntityAccidentStatus.class, dtoA.getIdAccidentStatus()));

        accident.setIdEmployee(em.getReference(EntityEmployee.class, dtoA.getIdEmployee())); //ID del empleado que fue víctima
        accident.setIdLocation(em.getReference(EntityLocation.class, dtoA.getIdLocation()));
        accident.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));
        accident.setSentBy(em.getReference(EntityEmployee.class, idEmployee)); //ID del empleado que reportó

        EntityAccident saved = objRepoA.save(accident);

        //Envío de correo (try/catch para separar en caso no se pueda mandar el correo)
        try {
            //Obtenemos los admins (Todos ellos van a recibir el correo)
            List<String> adminEmails = objRepoE.findByIdBusiness_IdBusinessAndIdRole_RoleNameIgnoreCaseAndEmployeeEmailIsNotNullAndEndDateIsNull(idBusiness.toUpperCase(), "Administrador")
                    .stream()
                    .map(EntityEmployee::getEmployeeEmail)
                    .distinct()
                    .toList();

            //Enviar uno por uno (mejor trazabilidad)
            for (String to : adminEmails) {
                serviceEmailSender.sendAccidentReportedTemplate(
                        to,
                        "Se ha reportado un nuevo accidente",
                        "RISKOR",
                        saved.getAccidentDate().toString(),
                        saved.getIdEmployee().getFirstName() + " " + saved.getIdEmployee().getLastName(),
                        saved.getIdLocation().getLocationName(),
                        saved.getSentBy().getFirstName() + " " + saved.getSentBy().getLastName(),
                        saved.getSentBy().getEmployeeEmail()
                );
            }
        } catch (Exception mailEx) {
            System.err.println("ADVERTENCIA: notificación a administradores falló. Detalle: " + mailEx.getMessage());
        }

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

    private DTOAccident convertToDTOA(EntityAccident accident){
        DTOAccident dtoA = new DTOAccident();
        dtoA.setIdAccident(accident.getIdAccident());
        dtoA.setDescription(accident.getDescription());
        dtoA.setAccidentDate(accident.getAccidentDate());
        dtoA.setReportAccident(accident.getReportAccident());
        dtoA.setIdAccidentCategory(accident.getIdAccidentCategory() != null ? accident.getIdAccidentCategory().getIdAccidentCategory() : null);
        dtoA.setAccidentCategory(accident.getIdAccidentCategory() != null ? accident.getIdAccidentCategory().getAccidentCategory() : null);
        dtoA.setIdAccidentType(accident.getIdAccidentType() != null ? accident.getIdAccidentType().getIdAccidentType() : null);
        dtoA.setAccidentType(accident.getIdAccidentType() != null ? accident.getIdAccidentType().getAccidentType() : null);
        dtoA.setIdAccidentSeverity(accident.getIdAccidentSeverity() != null ? accident.getIdAccidentSeverity().getIdAccidentSeverity() : null);
        dtoA.setAccidentSeverity(accident.getIdAccidentSeverity() != null ? accident.getIdAccidentSeverity().getAccidentSeverity() : null);
        dtoA.setIdAccidentStatus(accident.getIdAccidentStatus() != null ? accident.getIdAccidentStatus().getIdAccidentStatus() : null);
        dtoA.setAccidentStatus(accident.getIdAccidentStatus() != null ? accident.getIdAccidentStatus().getAccidentStatus() : null);
        dtoA.setIdEmployee(accident.getIdEmployee() != null ? accident.getIdEmployee().getIdEmployee() : null);
        dtoA.setEmployee(accident.getIdEmployee() != null ? accident.getIdEmployee().getFirstName() + accident.getIdEmployee().getLastName() : null);
        dtoA.setIdLocation(accident.getIdLocation() != null ? accident.getIdLocation().getIdLocation() : null);
        dtoA.setLocation(accident.getIdLocation() != null ? accident.getIdLocation().getLocationName() : null);
        dtoA.setIdBusiness(accident.getIdBusiness() != null ? accident.getIdBusiness().getIdBusiness() : null);
        dtoA.setBusiness(accident.getIdBusiness() != null ? accident.getIdBusiness().getNameBusiness() : null);
        dtoA.setSentBy(accident.getIdEmployee() != null ? accident.getSentBy().getFirstName() + " " + accident.getSentBy().getLastName() : null);

        return dtoA;
    }
}
