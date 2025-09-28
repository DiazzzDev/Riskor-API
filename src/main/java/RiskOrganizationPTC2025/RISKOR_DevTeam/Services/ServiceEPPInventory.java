package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPInventory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypeEPPControl;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPInventory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEPPInventory;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceEPPInventory {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryEPPInventory objRepoEPPInventory;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public DTOEPPInventory getEPPInventoryById(String idBusiness, String idEPPInventory) {
        EntityEPPInventory entityEPPInventory = objRepoEPPInventory.findByIdEPPInventoryAndIdBusiness_IdBusiness(idEPPInventory, idBusiness).orElseThrow(() -> new EntityNotFoundException("EPP no encontrado con ID: " + idEPPInventory));
        return convertTOEPPInventoryDTO(entityEPPInventory);
    }

    @Transactional(readOnly = true)
    public List<DTOEPPInventory> getAllEPPInventory(String idBusiness){
        List<EntityEPPInventory> eppInventoryList = objRepoEPPInventory.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return eppInventoryList.stream().map(this::convertTOEPPInventoryDTO).collect(Collectors.toList());
    }

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOEPPInventory> getAllEPPInventory(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEPPInventory> eppInventoryList = objRepoEPPInventory.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return eppInventoryList.map(this::convertTOEPPInventoryDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOEPPInventory postEPPInventory(@Valid DTOEPPInventory DTOEPPInventory, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOEPPInventory == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityEPPInventory eppInventory = objRepoEPPInventory.save(convertTOEPPInventoryEntity(DTOEPPInventory, idBusiness));
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOEPPInventoryDTO(eppInventory);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOEPPInventory putEPPInventory(DTOEPPInventory dtoeppInventory, String idEPPInventory, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoeppInventory == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Buscamos si existe el registro con el ID proporcionado
        EntityEPPInventory objEntityEPPI = objRepoEPPInventory.findByIdEPPInventoryAndIdBusiness_IdBusiness(idEPPInventory, idBusiness).orElseThrow(() -> new EntityNotFoundException("Inventario de EPP no encontrado con ID: " + idEPPInventory));

        //Actualizamos los campos
        objEntityEPPI.setNameEPP(dtoeppInventory.getNameEPP());
        objEntityEPPI.setDescription(dtoeppInventory.getDescription());
        objEntityEPPI.setTotalQuantity(dtoeppInventory.getTotalQuantity());
        objEntityEPPI.setAvailableQuantity(dtoeppInventory.getAvailableQuantity());
        objEntityEPPI.setIdTypeEPPControl(em.getReference(EntityTypeEPPControl.class, dtoeppInventory.getIdTypeEPPControl()));

        //Retornamos el DTO actualizado
        return convertTOEPPInventoryDTO(objEntityEPPI); //JPA sincroniza por uso de @transactional
    }

    public boolean deleteEPPInventory(String idEPPInventory, String idBusiness) {
        if (!objRepoEPPInventory.existsByIdEPPInventoryAndIdBusiness_IdBusiness(idEPPInventory, idBusiness.toUpperCase())) { return false; }

        objRepoEPPInventory.deleteByIdEPPInventoryAndIdBusiness_IdBusiness(idEPPInventory, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOEPPInventory convertTOEPPInventoryDTO(EntityEPPInventory eppInventory){
        DTOEPPInventory objEPPInventoryDTO = new DTOEPPInventory();
        objEPPInventoryDTO.setIdEPPInventory(eppInventory.getIdEPPInventory());
        objEPPInventoryDTO.setNameEPP(eppInventory.getNameEPP());
        objEPPInventoryDTO.setDescription(eppInventory.getDescription());
        objEPPInventoryDTO.setTotalQuantity(eppInventory.getTotalQuantity());
        objEPPInventoryDTO.setAvailableQuantity(eppInventory.getAvailableQuantity());
        objEPPInventoryDTO.setIdTypeEPPControl(eppInventory.getIdTypeEPPControl().getIdTypeEPPControl());
        objEPPInventoryDTO.setTypeEPPControl(eppInventory.getIdTypeEPPControl().getTypeEPPControl());

        objEPPInventoryDTO.setIdBusiness(eppInventory.getIdBusiness().getIdBusiness());
        return objEPPInventoryDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityEPPInventory convertTOEPPInventoryEntity(DTOEPPInventory DTOEPPInventory, String idBusiness){
        EntityEPPInventory objEntityEPPInventory = new EntityEPPInventory();
        objEntityEPPInventory.setNameEPP(DTOEPPInventory.getNameEPP());
        objEntityEPPInventory.setDescription(DTOEPPInventory.getDescription());
        objEntityEPPInventory.setTotalQuantity(DTOEPPInventory.getTotalQuantity());
        objEntityEPPInventory.setAvailableQuantity(DTOEPPInventory.getAvailableQuantity());
        objEntityEPPInventory.setIdTypeEPPControl(em.getReference(EntityTypeEPPControl.class, DTOEPPInventory.getIdTypeEPPControl()));

        objEntityEPPInventory.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
        return objEntityEPPInventory;
    }
}
