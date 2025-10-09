package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOControlSDSSO;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeControlSafetyDevice;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeEPPControl;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTypeControlSafetyDevice;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTypeControlSafetyDevice {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryTypeControlSafetyDevice objRepoTCSD;

    @PersistenceContext
    private EntityManager em; //Ayuda a evitar cargar objetos completos en FK

    @Transactional(readOnly = true)
    public DTOTypeControlSafetyDevice getControlSDSSOById(String idControlSDSSO, String idBusiness){
        var safetyDevice = objRepoTCSD.findByIdTypeControlSDAndIdBusiness_IdBusiness(idControlSDSSO, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Tipo de control de dispositivo de seguridad no encontrado"));
        return convertTOTypeCSDDTO(safetyDevice);
    }

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOTypeControlSafetyDevice> getAllTypeControlSD(String idBusiness, int page, int size){
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var entities = objRepoTCSD.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return entities.map(this::convertTOTypeCSDDTO);
    }

    public DTOTypeControlSafetyDevice postTypeControlSD(@Valid DTOTypeControlSafetyDevice dtoTypeEPPControl, String idBusiness) {
        if (dtoTypeEPPControl == null)  throw new EntityNotFoundException("No pueden haber campos vacíos"); //Validamos que el JSON no esté vacío

        EntityTypeControlSafetyDevice saved = objRepoTCSD.save(convertToEntityTypeCSD(dtoTypeEPPControl, idBusiness));
        return convertTOTypeCSDDTO(saved);
    }

    public DTOTypeControlSafetyDevice putTypeControlSD(@Valid DTOTypeControlSafetyDevice dtoTypeEPPControl, String idTypeControlSD, String idBusiness) {
        if (dtoTypeEPPControl == null)  throw new EntityNotFoundException("No pueden haber campos vacíos");

        EntityTypeControlSafetyDevice typeEPPControl = objRepoTCSD.findByIdTypeControlSDAndIdBusiness_IdBusiness(idTypeControlSD, idBusiness.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Tipo de EPP no encontrado"));

        typeEPPControl.setTypeControlSD(dtoTypeEPPControl.getTypeControlSD());
        typeEPPControl.setIdTypeCategoryCSD(em.getReference(EntityTypeCategoryControlSD.class, dtoTypeEPPControl.getIdTypeCategoryCSD()));

        return convertTOTypeCSDDTO(typeEPPControl); //Ya no se necesita SAVE por uso de @Transactional
    }

    public boolean removeTypeControlSD(String idTypeControlSD, String idBusiness) {
        if (!objRepoTCSD.existsByIdTypeControlSDAndIdBusiness_IdBusiness(idTypeControlSD, idBusiness.toUpperCase())) { return false; }

        objRepoTCSD.deleteByIdTypeControlSDAndIdBusiness_IdBusiness(idTypeControlSD, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOTypeControlSafetyDevice convertTOTypeCSDDTO(EntityTypeControlSafetyDevice typeCSD){
        DTOTypeControlSafetyDevice objTypeCSDDTO = new DTOTypeControlSafetyDevice();
        objTypeCSDDTO.setIdTypeControlSD(typeCSD.getIdTypeControlSD());
        objTypeCSDDTO.setTypeControlSD(typeCSD.getTypeControlSD());
        objTypeCSDDTO.setIdTypeCategoryCSD(typeCSD.getIdTypeCategoryCSD().getIdTypeCategoryCSD());
        objTypeCSDDTO.setIdBusiness(typeCSD.getIdBusiness().getIdBusiness());

        return objTypeCSDDTO;
    }

    public EntityTypeControlSafetyDevice convertToEntityTypeCSD(DTOTypeControlSafetyDevice typeCSDDTO, String idBusiness) {
        EntityTypeControlSafetyDevice entityTypeCSD = new EntityTypeControlSafetyDevice();
        entityTypeCSD.setIdTypeControlSD(typeCSDDTO.getIdTypeControlSD());
        entityTypeCSD.setTypeControlSD(typeCSDDTO.getTypeControlSD());
        entityTypeCSD.setIdTypeCategoryCSD(em.getReference(EntityTypeCategoryControlSD.class, typeCSDDTO.getIdTypeCategoryCSD()));
        entityTypeCSD.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entityTypeCSD;
    }
}