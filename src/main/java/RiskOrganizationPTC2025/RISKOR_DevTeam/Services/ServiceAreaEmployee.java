package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAreaEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAreaEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAreaEmployee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceAreaEmployee {
    @Autowired
    private RepositoryAreaEmployee objRepoAE;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo y las otras FK sin cargar todo desde la db

    //GET - Mostrar empleados que pertenecen a todas las áreas
    @Transactional(readOnly = true)
    public List<DTOAreaEmployee> getAreaEmployees(String idBusiness){
        List<EntityAreaEmployee> areaEmployees = objRepoAE.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return areaEmployees.stream().map(this::convertToDTOAE).collect(Collectors.toList());
    }

    //POST - Método para registrar UN EMPLEADO en un área
    public DTOAreaEmployee postAreaEmployee(@Valid DTOAreaEmployee dtoAE, String idBusiness){
        //Validaciones
        if (dtoAE == null) throw new IllegalArgumentException("No pueden haber campos vacios");
        if (dtoAE.getIdArea() == null || dtoAE.getIdArea().isBlank()) throw new IllegalArgumentException("idArea es obligatorio");
        if (dtoAE.getIdEmployee() == null || dtoAE.getIdEmployee().isBlank()) throw new IllegalArgumentException("idEmployee es obligatorio");

        //Verificamos si el empleado ya existe en el área para evitar que un mismo empleado sea registrado por segunda vez en la misma área
        boolean duplicated = objRepoAE.existsByIdArea_IdAreaAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(dtoAE.getIdArea(), dtoAE.getIdEmployee(), idBusiness.toUpperCase());
        if (duplicated) throw new IllegalArgumentException("El empleado ya está asignado a esa área en esta empresa");

        EntityAreaEmployee saved = objRepoAE.save(convertToEAE(dtoAE, idBusiness)); //Guardamos el empleado en el área
        return convertToDTOAE(saved); //Convertimos el área a DTO para mostrar en la respuesta los datos insertados
    }

    //POST para MUCHOS EMPLEADOS en un área ya existente
    public List<DTOAreaEmployee> assignEmployeesOnArea(String idBusiness, String idArea, List<String> employeeIds) {
        if (idArea == null || idArea.isBlank()) throw new IllegalArgumentException("idArea es obligatorio");
        if (employeeIds == null || employeeIds.isEmpty()) return List.of();

        EntityArea areaRef = em.getReference(EntityArea.class, idArea);
        EntityBusinessInfo bizRef = em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase());

        // Deduplicar entradas nulas/repetidas
        Set<String> uniqueEmpIds = new LinkedHashSet<>();
        for (String idEmp : employeeIds) {
            if (idEmp != null && !idEmp.isBlank()) uniqueEmpIds.add(idEmp);
        }

        List<DTOAreaEmployee> out = new ArrayList<>(uniqueEmpIds.size());
        for (String idEmp : uniqueEmpIds) {
            //Evitar duplicado lógico
            boolean exists = objRepoAE.existsByIdArea_IdAreaAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(idArea, idEmp, idBusiness.toUpperCase());
            if (exists) continue;

            // Construir y guardar vínculo
            EntityAreaEmployee link = new EntityAreaEmployee();
            link.setIdArea(areaRef);
            link.setIdEmployee(em.getReference(EntityEmployee.class, idEmp));
            link.setIdBusiness(bizRef);

            try {
                EntityAreaEmployee saved = objRepoAE.save(link);
                out.add(convertToDTOAE(saved));
            } catch (DataIntegrityViolationException dup) {
                //Si otro hilo insertó el mismo par (área, empleado, empresa) entre el exists y el save
                //lo ignoramos silenciosamente (ya está asignado)
            }
        }
        return out;
    }

    //PUT - Método para modificar el registro de un empleado dentro de un área
    public DTOAreaEmployee putAreaEmployee(@Valid DTOAreaEmployee dtoAE, String idAreaEmployee, String idBusiness) {
        if (dtoAE == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityAreaEmployee areaEmployee = objRepoAE.findByIdAreaEmployeeAndIdBusiness_IdBusiness(idAreaEmployee, idBusiness).orElseThrow(() -> new EntityNotFoundException("Empleado en el área no encontrado"));

        //Actualizar referencias (JPA sincroniza por @Transactional)
        //Si no se entregaron valores en blanco en el ID del área se va a actualizar este campo
        if (dtoAE.getIdArea() != null && !dtoAE.getIdArea().isBlank()) {
            areaEmployee.setIdArea(em.getReference(EntityArea.class, dtoAE.getIdArea())); //Se manda a llamar a la clase por Lazy Fetch con FKs
        }

        //Si no se entregaron valores en blanco en el ID del empleado se va a actualizar este campo
        if (dtoAE.getIdEmployee() != null && !dtoAE.getIdEmployee().isBlank()) {
            areaEmployee.setIdEmployee(em.getReference(EntityEmployee.class, dtoAE.getIdEmployee()));
        }
        return convertToDTOAE(areaEmployee);
    }

    //DELETE - Eliminar el registro de un empleado dentro de un área
    public boolean removeAreaEmployee(String idAreaEmployee, String idBusiness) {
        if (idAreaEmployee == null || idAreaEmployee.trim().isEmpty()) throw new IllegalArgumentException("El ID del área no puede ser nulo o vacío");

        //Verificamos la existencia del registro que queremos eliminar
        EntityAreaEmployee entity = objRepoAE.findByIdAreaEmployeeAndIdBusiness_IdBusiness(idAreaEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado en el área no encontrado"));

        objRepoAE.delete(entity); //Eliminamos llamando al método delete del repositorio
        return true;
    }

    private DTOAreaEmployee convertToDTOAE(EntityAreaEmployee areaEmployee){
        DTOAreaEmployee dtoAE = new DTOAreaEmployee();
        dtoAE.setIdAreaEmployee(areaEmployee.getIdAreaEmployee());
        dtoAE.setIdArea(areaEmployee.getIdArea() != null ? areaEmployee.getIdArea().getIdArea() : null);
        dtoAE.setIdEmployee(areaEmployee.getIdEmployee() != null ? areaEmployee.getIdEmployee().getIdEmployee() : null);
        dtoAE.setIdBusiness(areaEmployee.getIdBusiness() != null ? areaEmployee.getIdBusiness().getIdBusiness() : null);

        return dtoAE;
    }

    private EntityAreaEmployee convertToEAE(DTOAreaEmployee dtoAE, String idBusiness){
        EntityAreaEmployee areaEmployee = new EntityAreaEmployee();
        areaEmployee.setIdArea(em.getReference(EntityArea.class, dtoAE.getIdArea()));
        areaEmployee.setIdEmployee(em.getReference(EntityEmployee.class, dtoAE.getIdEmployee()));
        areaEmployee.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

        return areaEmployee;
    }
}