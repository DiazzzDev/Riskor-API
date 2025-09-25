package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionResult;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspectionResult;
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
public class ServiceInspectionResult {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryInspectionResult objRepoInspectionR;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOInspectionResult> getAllInspectionR(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityInspectionResult> loanDetails = objRepoInspectionR.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return loanDetails.map(this::convertTOInspectionRDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOInspectionResult postInspectionR(@Valid DTOInspectionResult DTOInspectionResult, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOInspectionResult == null){
            throw new IllegalArgumentException("No pueden haber campos vacíos");
        }
        //Caso contrario, se procede con la inserción de datos (POST)
        EntityInspectionResult objeInspectionRSaved = objRepoInspectionR.save(convertTOInspectionREntity(DTOInspectionResult, idBusiness));
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOInspectionRDTO(objeInspectionRSaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOInspectionResult putInspectionResult(@Valid DTOInspectionResult dtoInspectionResult, String idInspectionResult, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoInspectionResult == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Buscamos si existe el registro con el ID proporcionado
        EntityInspectionResult result = objRepoInspectionR.findByIdInspectionResultAndIdBusiness_IdBusiness(idInspectionResult, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Resultado de inspección no encontrado con ID: " + idInspectionResult));

        //Actualizamos los campos, si no tienen nada permanecen como eran originalmente
        if (dtoInspectionResult.getIdComplianceStatus() != null) {
            result.setIdComplianceStatus(em.getReference(EntityComplianceStatus.class, dtoInspectionResult.getIdComplianceStatus()));        }

        //Retornamos el DTO actualizado
        return convertTOInspectionRDTO(result);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteInspectionResult(String idInspectionResult, String idBusiness){
        if (!objRepoInspectionR.existsByIdInspectionResultAndIdBusiness_IdBusiness(idInspectionResult, idBusiness.toUpperCase())) { return false; }

        objRepoInspectionR.deleteByIdInspectionResultAndIdBusiness_IdBusiness(idInspectionResult, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOInspectionResult convertTOInspectionRDTO(EntityInspectionResult inspectionIResult){
        DTOInspectionResult objInspectionRDTO = new DTOInspectionResult();
        objInspectionRDTO.setIdInspectionResult(inspectionIResult.getIdInspectionResult());
        objInspectionRDTO.setIdInspection(inspectionIResult.getIdInspection().getIdInspection());
        objInspectionRDTO.setIdComplianceStatus(inspectionIResult.getIdComplianceStatus().getIdComplianceStatus());
        objInspectionRDTO.setIdBusiness(inspectionIResult.getIdBusiness().getIdBusiness());

        return objInspectionRDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityInspectionResult convertTOInspectionREntity(DTOInspectionResult dtoInspectionResult, String idBusiness){
        EntityInspectionResult objEntityInspectionR = new EntityInspectionResult();
        objEntityInspectionR.setIdInspection(em.getReference(EntityInspection.class, dtoInspectionResult.getIdInspection()));
        objEntityInspectionR.setIdComplianceStatus(em.getReference(EntityComplianceStatus.class, dtoInspectionResult.getIdComplianceStatus()));
        objEntityInspectionR.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityInspectionR;
    }
}