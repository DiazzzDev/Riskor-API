package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityControlSDSSO;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityMaintenanceControlSD;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMaintenanceControlSD;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryMaintenanceControlSD;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceMaintenanceControlSD {
    @Autowired //Inyectamos el repositorio
    private RepositoryMaintenanceControlSD objRepoMaintenanceCSD;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOMaintenanceControlSD> getAllMaintenanceCSD(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityMaintenanceControlSD> maintenances = objRepoMaintenanceCSD.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return maintenances.map(this::convertTOMaintenanceSDDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOMaintenanceControlSD postMaintenanceC(DTOMaintenanceControlSD DTOMaintenanceControlSD, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOMaintenanceControlSD == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityMaintenanceControlSD saved = objRepoMaintenanceCSD.save(convertTOMaintenanceCEntity(DTOMaintenanceControlSD, idBusiness));
        return convertTOMaintenanceSDDTO(saved); //Finalmente, retornamos los valores en formato JSON con código 201 CREATED
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOMaintenanceControlSD putMaintenanceControlSD(DTOMaintenanceControlSD dtoMaintenance, String idMaintenance, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoMaintenance == null) throw new IllegalArgumentException("No pueden haber campos vacíos");


        //Buscamos si existe el registro con el ID proporcionado
        EntityMaintenanceControlSD maintenanceControlSD = objRepoMaintenanceCSD.findByIdMaintenanceControlSDAndIdBusiness_IdBusiness(idMaintenance, idBusiness).orElseThrow(() -> new EntityNotFoundException("Mantenimiento no encontrado con ID: " + idMaintenance));

        //Actualizamos los campos
        maintenanceControlSD.setDateMaintenance(dtoMaintenance.getDateMaintenance());
        maintenanceControlSD.setDescription(dtoMaintenance.getDescription());
        maintenanceControlSD.setCarriedOutBy(dtoMaintenance.getCarriedOutBy());
        maintenanceControlSD.setObservation(dtoMaintenance.getObservation());

        //Si no se cambió nada en la FK se mantiene igual
        if (dtoMaintenance.getIdServiceDeviceSSO() != null) {
            maintenanceControlSD.setIdServiceDeviceSSO(em.getReference(EntityControlSDSSO.class, dtoMaintenance.getIdServiceDeviceSSO()));
        }

        //Retornamos el DTO actualizado
        return convertTOMaintenanceSDDTO(maintenanceControlSD);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteMaintenanceControlSD(String idMaintenanceControlSD, String idBusiness){
        if (!objRepoMaintenanceCSD.existsByIdMaintenanceControlSDAndIdBusiness_IdBusiness(idMaintenanceControlSD, idBusiness.toUpperCase())) { return false; }

        objRepoMaintenanceCSD.deleteByIdMaintenanceControlSDAndIdBusiness_IdBusiness(idMaintenanceControlSD, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOMaintenanceControlSD convertTOMaintenanceSDDTO(EntityMaintenanceControlSD maintenanceControlSD){
        DTOMaintenanceControlSD objMaintenanceCSDDTO = new DTOMaintenanceControlSD();
        objMaintenanceCSDDTO.setIdMaintenanceControlSD(maintenanceControlSD.getIdMaintenanceControlSD());
        objMaintenanceCSDDTO.setDateMaintenance(maintenanceControlSD.getDateMaintenance());
        objMaintenanceCSDDTO.setDescription(maintenanceControlSD.getDescription());
        objMaintenanceCSDDTO.setCarriedOutBy(maintenanceControlSD.getCarriedOutBy());
        objMaintenanceCSDDTO.setObservation(maintenanceControlSD.getObservation());
        objMaintenanceCSDDTO.setIdServiceDeviceSSO(maintenanceControlSD.getIdServiceDeviceSSO().getIdServiceDeviceSSO());
        objMaintenanceCSDDTO.setIdBusiness(maintenanceControlSD.getIdBusiness().getIdBusiness());

        return objMaintenanceCSDDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityMaintenanceControlSD convertTOMaintenanceCEntity(DTOMaintenanceControlSD dtoMaintenanceControlSD, String idBusiness){
        EntityMaintenanceControlSD objEntityMaintenanceC = new EntityMaintenanceControlSD();
        objEntityMaintenanceC.setDateMaintenance(dtoMaintenanceControlSD.getDateMaintenance());
        objEntityMaintenanceC.setDescription(dtoMaintenanceControlSD.getDescription());
        objEntityMaintenanceC.setCarriedOutBy(dtoMaintenanceControlSD.getCarriedOutBy());
        objEntityMaintenanceC.setObservation(dtoMaintenanceControlSD.getObservation());
        objEntityMaintenanceC.setIdServiceDeviceSSO(em.getReference(EntityControlSDSSO.class, dtoMaintenanceControlSD.getIdServiceDeviceSSO()));
        objEntityMaintenanceC.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityMaintenanceC;
    }
}