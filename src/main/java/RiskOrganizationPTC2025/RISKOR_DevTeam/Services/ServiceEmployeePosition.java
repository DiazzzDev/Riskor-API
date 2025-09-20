package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployeePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEmployeePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEmployeePosition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceEmployeePosition {
    @Autowired
    private RepositoryEmployeePosition objRepoEP;

    @PersistenceContext
    private EntityManager em; //Ayuda a evitar cargar objetos completos en FK

    @Transactional(readOnly = true)
    public DTOEmployeePosition getPositionById(String idBusiness, String idEmployeePosition) {
        EntityEmployeePosition position = objRepoEP.findByIdEmployeePositionAndIdBusiness_IdBusiness(idEmployeePosition, idBusiness.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo para empleado dentro de esta empresa"));
        return convertToDTOEP(position);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployeePosition> getEmployeePosition(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEmployeePosition> positions = objRepoEP.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return positions.map(this::convertToDTOEP);
    }

    //POST
    public DTOEmployeePosition postEmployeePosition(@Valid DTOEmployeePosition dtoEP, String idBusiness) {
        if (dtoEP == null){ throw new IllegalArgumentException("No pueden haber campos vacíos"); }

        EntityEmployeePosition saved = objRepoEP.save(convertToEEP(dtoEP, idBusiness));
        return convertToDTOEP(saved);
    }

    public DTOEmployeePosition putEmployeePosition(@Valid DTOEmployeePosition dtoEP, String idEmployeePosition, String idBusiness) {
        if (dtoEP == null) { throw new EntityNotFoundException("No pueden haber campos vacíos"); }

        EntityEmployeePosition employeePosition = objRepoEP.findByIdEmployeePositionAndIdBusiness_IdBusiness(idEmployeePosition, idBusiness).orElseThrow(() -> new IllegalArgumentException("Cargo laboral no encontrado"));

        employeePosition.setEmployeePosition(dtoEP.getEmployeePosition());

        //EntityEmployeePosition employeePosition = objRepoEP.save(employeePositionExists); Ya no se necesita por uso de @Transactional
        return convertToDTOEP(employeePosition);
    }

    public boolean removeEmployeePosition(String idEmployeePosition, String idBusiness) {
        if (!objRepoEP.existsByIdEmployeePositionAndIdBusiness_IdBusiness(idEmployeePosition, idBusiness)) { return false; }

        objRepoEP.deleteByIdEmployeePositionAndIdBusiness_IdBusiness(idEmployeePosition, idBusiness);
        return true;
    }

    private DTOEmployeePosition convertToDTOEP(EntityEmployeePosition position){
        DTOEmployeePosition dtoEP = new DTOEmployeePosition();
        dtoEP.setIdEmployeePosition(position.getIdEmployeePosition());
        dtoEP.setEmployeePosition(position.getEmployeePosition());
        dtoEP.setIdBusiness(position.getIdBusiness() != null ? position.getIdBusiness().getIdBusiness() : null);
        return dtoEP;
    }

    private EntityEmployeePosition convertToEEP(DTOEmployeePosition dtoEP, String idBusiness){
        EntityEmployeePosition employeePosition = new EntityEmployeePosition();
        employeePosition.setEmployeePosition(dtoEP.getEmployeePosition());
        employeePosition.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
        return employeePosition;
    }
}
