package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOControlSDSSO;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryControlSDSSO;
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
public class ServiceControlSDSSO {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryControlSDSSO objRepoControlSDSSO;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Transactional(readOnly = true)
    public DTOControlSDSSO getControlSDSSOById(String idBusiness, String idControlSDSSO) {
        EntityControlSDSSO position = objRepoControlSDSSO.findByIdServiceDeviceSSOAndIdBusiness_IdBusiness(idControlSDSSO, idBusiness.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo para empleado dentro de esta empresa"));
        return convertTOControlSDSSODTO(position);
    }

    @Transactional(readOnly = true)
    public Page<DTOControlSDSSO> getAllControlSDSSO(int page, int size, String idBusiness) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityControlSDSSO> permissionPage = objRepoControlSDSSO.findByIdBusiness_IdBusiness(pageable, idBusiness.toUpperCase());
        return permissionPage.map(this::convertTOControlSDSSODTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOControlSDSSO postControlSDSSO(@Valid DTOControlSDSSO dtoControlSDSSO, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (dtoControlSDSSO == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityControlSDSSO objeControlSDSSOSaved = objRepoControlSDSSO.save(convertTOControlSDSSOEntity(dtoControlSDSSO, idBusiness));
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOControlSDSSODTO(objeControlSDSSOSaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOControlSDSSO putControlSDSSO(@Valid DTOControlSDSSO dtoControlSDSSO, String idControlSDSSO, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoControlSDSSO == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Buscamos si existe el registro con el ID proporcionado
        EntityControlSDSSO controlSDSSO = objRepoControlSDSSO.findByIdServiceDeviceSSOAndIdBusiness_IdBusiness(idControlSDSSO, idBusiness).orElseThrow(() -> new EntityNotFoundException("Control SDSSO no encontrado con ID: " + idControlSDSSO));

        //Actualizamos los campos
        controlSDSSO.setNameServiceDevice(dtoControlSDSSO.getNameServiceDevice());
        controlSDSSO.setDescription(dtoControlSDSSO.getDescription());
        controlSDSSO.setInstallationDate(dtoControlSDSSO.getInstallationDate());

        if (dtoControlSDSSO.getIdEmployee() != null) {
            controlSDSSO.setIdEmployee(em.getReference(EntityEmployee.class, dtoControlSDSSO.getIdEmployee()));
        }
        if (dtoControlSDSSO.getIdLocation() != null) {
            controlSDSSO.setIdLocation(em.getReference(EntityLocation.class, dtoControlSDSSO.getIdLocation()));
        }
        if (dtoControlSDSSO.getIdControlSDStatus() != null) {
            controlSDSSO.setIdControlSDStatus(em.getReference(EntityControlSDStatus.class, dtoControlSDSSO.getIdControlSDStatus()));
        }

        //Retornamos el DTO actualizado
        return convertTOControlSDSSODTO(controlSDSSO); //Omitimos SAVE por uso de @Transactional
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteControlSDSSO(String idServiceDeviceSSO, String idBusiness){
        if (!objRepoControlSDSSO.existsByIdServiceDeviceSSOAndIdBusiness_IdBusiness(idServiceDeviceSSO, idBusiness.toUpperCase())) { return false; }

        objRepoControlSDSSO.deleteByIdServiceDeviceSSOAndIdBusiness_IdBusiness(idServiceDeviceSSO, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOControlSDSSO convertTOControlSDSSODTO(EntityControlSDSSO controlSDSSO){
        DTOControlSDSSO objControlSDSSODTO = new DTOControlSDSSO();
        objControlSDSSODTO.setIdServiceDeviceSSO(controlSDSSO.getIdServiceDeviceSSO());
        objControlSDSSODTO.setNameServiceDevice(controlSDSSO.getNameServiceDevice());
        objControlSDSSODTO.setDescription(controlSDSSO.getDescription());
        objControlSDSSODTO.setInstallationDate(controlSDSSO.getInstallationDate());
        objControlSDSSODTO.setMaintenanceDate(controlSDSSO.getMaintenanceDate());
        objControlSDSSODTO.setIdEmployee(controlSDSSO.getIdEmployee().getIdEmployee());
        objControlSDSSODTO.setIdTypeControlSD(controlSDSSO.getIdTypeControlSD().getIdTypeControlSD());
        objControlSDSSODTO.setIdLocation(controlSDSSO.getIdLocation().getIdLocation());
        objControlSDSSODTO.setIdControlSDStatus(controlSDSSO.getIdControlSDStatus().getIdControlSDStatus());
        //Si el objeto idBusiness existe en la entidad area, obtén su ID; si no, simplemente asigna null - Esto por el uso de FETCH LAZY
        objControlSDSSODTO.setIdBusiness(controlSDSSO.getIdBusiness() != null ? controlSDSSO.getIdBusiness().getIdBusiness() : null);

        return objControlSDSSODTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityControlSDSSO convertTOControlSDSSOEntity(DTOControlSDSSO dtoControlSDSSO, String idBusiness){
        EntityControlSDSSO objEntityControlSDSSO = new EntityControlSDSSO();
        objEntityControlSDSSO.setNameServiceDevice(dtoControlSDSSO.getNameServiceDevice());
        objEntityControlSDSSO.setDescription(dtoControlSDSSO.getDescription());
        objEntityControlSDSSO.setInstallationDate(dtoControlSDSSO.getInstallationDate());
        objEntityControlSDSSO.setMaintenanceDate(dtoControlSDSSO.getMaintenanceDate());
        //Llamamos foráneas desde el entity manager por uso de Lazy fetch
        objEntityControlSDSSO.setIdEmployee(em.getReference(EntityEmployee.class, dtoControlSDSSO.getIdEmployee()));
        objEntityControlSDSSO.setIdTypeControlSD(em.getReference(EntityTypeControlSafetyDevice.class, dtoControlSDSSO.getIdTypeControlSD()));
        objEntityControlSDSSO.setIdLocation(em.getReference(EntityLocation.class, dtoControlSDSSO.getIdLocation()));
        objEntityControlSDSSO.setIdControlSDStatus(em.getReference(EntityControlSDStatus.class, dtoControlSDSSO.getIdControlSDStatus()));
        objEntityControlSDSSO.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityControlSDSSO;
    }
}
