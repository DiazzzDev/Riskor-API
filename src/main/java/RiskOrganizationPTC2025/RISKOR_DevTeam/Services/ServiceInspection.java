package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspection;
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
import java.time.LocalDate;

@Service
@Transactional
public class ServiceInspection {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryInspection objRepoInspection;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOInspection> getAllInspection(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityInspection> inspections = objRepoInspection.findByIdBusiness_IdBusiness(idBusiness, pageable);
        return inspections.map(this::convertTOInspectionDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOInspection postInspection(@Valid DTOInspection DTOInspection, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOInspection == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityInspection objeInspectionSaved = objRepoInspection.save(convertTOInspectionEntity(DTOInspection, idBusiness));
        return convertTOInspectionDTO(objeInspectionSaved); //Finalmente, retornamos los valores en formato JSON con 201 CREATED
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOInspection putInspection(@Valid DTOInspection dtoInspection, String idInspection, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoInspection == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Buscamos si existe el registro con el ID proporcionado
        EntityInspection inspection = objRepoInspection.findByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Inspección no encontrada con ID: " + idInspection));

        //Actualizamos los campos
        inspection.setObservation(dtoInspection.getObservation());

        //Si las FKs no son modificadas en el PUT se mantendrán en su valor original
        if (dtoInspection.getIdArea() != null) {
            inspection.setIdArea(em.getReference(EntityArea.class, dtoInspection.getIdArea()));
        }
        if (dtoInspection.getIdInspectionType() != null) {
            inspection.setIdInspectionType(em.getReference(EntityInspectionType.class, dtoInspection.getIdInspectionType()));
        }
        if (dtoInspection.getIdInspectionStatus() != null) {
            inspection.setIdInspectionStatus(em.getReference(EntityInspectionStatus.class, dtoInspection.getIdInspectionStatus()));
        }

        //Retornamos el DTO actualizado
        return convertTOInspectionDTO(inspection);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteInspection(String idInspection, String idBusiness){
        if (!objRepoInspection.existsByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase())) { return false; }

        objRepoInspection.deleteByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOInspection convertTOInspectionDTO(EntityInspection inspection){
        DTOInspection objInspectionDTO = new DTOInspection();
        objInspectionDTO.setIdInspection(inspection.getIdInspection());
        objInspectionDTO.setInspectionDate(inspection.getInspectionDate());
        objInspectionDTO.setObservation(inspection.getObservation());
        objInspectionDTO.setIdEmployee(inspection.getIdEmployee().getIdEmployee());
        objInspectionDTO.setIdArea(inspection.getIdArea().getIdArea());
        objInspectionDTO.setIdArea(inspection.getIdArea().getAreaName());
        objInspectionDTO.setIdInspectionType(inspection.getIdInspectionType().getIdInspectionType());
        objInspectionDTO.setIdInspectionType(inspection.getIdInspectionType().getInspectionType());
        objInspectionDTO.setIdInspectionStatus(inspection.getIdInspectionStatus().getIdInspectionStatus());
        objInspectionDTO.setIdInspectionStatus(inspection.getIdInspectionStatus().getInspectionStatus());
        objInspectionDTO.setIdBusiness(inspection.getIdBusiness().getIdBusiness());

        return objInspectionDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityInspection convertTOInspectionEntity(DTOInspection dtoInspection, String idBusiness){
        EntityInspection objEntityInspection = new EntityInspection();
        objEntityInspection.setInspectionDate(LocalDate.now());
        objEntityInspection.setObservation(dtoInspection.getObservation());
        objEntityInspection.setIdEmployee(em.getReference(EntityEmployee.class, dtoInspection.getIdEmployee()));
        objEntityInspection.setIdArea(em.getReference(EntityArea.class, dtoInspection.getIdArea()));
        objEntityInspection.setIdInspectionType(em.getReference(EntityInspectionType.class, dtoInspection.getIdInspectionType()));
        objEntityInspection.setIdInspectionStatus(em.getReference(EntityInspectionStatus.class, dtoInspection.getIdInspectionStatus()));
        objEntityInspection.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityInspection;
    }
}