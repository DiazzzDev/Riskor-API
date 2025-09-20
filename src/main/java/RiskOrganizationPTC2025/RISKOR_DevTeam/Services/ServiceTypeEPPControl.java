package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypeEPPControl;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeEPPControl;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTypeEPPControl;
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
public class ServiceTypeEPPControl {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryTypeEPPControl objRepoTEPPC;

    @PersistenceContext
    private EntityManager em; //Ayuda a evitar cargar objetos completos en FK

    @Transactional(readOnly = true)
    public DTOTypeEPPControl getTypeEPPById(String idBusiness, String idTypeEPPControl) {
        EntityTypeEPPControl typeEPPControl = objRepoTEPPC.findByIdTypeEPPControlAndIdBusiness_IdBusiness(idTypeEPPControl, idBusiness.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo para empleado dentro de esta empresa"));
        return convertTOTypeEPPCDTO(typeEPPControl);
    }

    @Transactional(readOnly = true)
    public List<DTOTypeEPPControl> getAllTypeEPPControlNoP(String idBusiness) {
        List<EntityTypeEPPControl> list = objRepoTEPPC.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return list.stream().map(this::convertTOTypeEPPCDTO).collect(Collectors.toList());
    }

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOTypeEPPControl> getAllTypeEPPControl(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityTypeEPPControl> typeEPPControlList = objRepoTEPPC.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return typeEPPControlList.map(this::convertTOTypeEPPCDTO);
    }

    public DTOTypeEPPControl postTypeEPPC(@Valid DTOTypeEPPControl dtoTypeEPPControl, String idBusiness) {
        if (dtoTypeEPPControl == null)  throw new EntityNotFoundException("No pueden haber campos vacíos"); //Validamos que el JSON no esté vacío

        EntityTypeEPPControl saved = objRepoTEPPC.save(convertToEntityEPP(dtoTypeEPPControl, idBusiness));
        return convertTOTypeEPPCDTO(saved);
    }

    public DTOTypeEPPControl putTypeEPPC(@Valid DTOTypeEPPControl dtoTypeEPPControl, String idTypeEPPControl, String idBusiness) {
        if (dtoTypeEPPControl == null)  throw new EntityNotFoundException("No pueden haber campos vacíos");

        EntityTypeEPPControl typeEPPControl = objRepoTEPPC.findByIdTypeEPPControlAndIdBusiness_IdBusiness(idTypeEPPControl, idBusiness.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Tipo de EPP no encontrado"));

        typeEPPControl.setTypeEPPControl(dtoTypeEPPControl.getTypeEPPControl());

        //Ya no se necesita SAVE por uso de @Transactional
        return convertTOTypeEPPCDTO(typeEPPControl);
    }

    public boolean removeTypeEPPC(String idTypeEPPControl, String idBusiness) {
        if (!objRepoTEPPC.existsByIdTypeEPPControlAndIdBusiness_IdBusiness(idTypeEPPControl, idBusiness.toUpperCase())) { return false; }

        objRepoTEPPC.deleteByIdTypeEPPControlAndIdBusiness_IdBusiness(idTypeEPPControl, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOTypeEPPControl convertTOTypeEPPCDTO(EntityTypeEPPControl typeEPPC){
        DTOTypeEPPControl objTypeEPPCDTO = new DTOTypeEPPControl();
        objTypeEPPCDTO.setIdTypeEPPControl(typeEPPC.getIdTypeEPPControl());
        objTypeEPPCDTO.setTypeEPPControl(typeEPPC.getTypeEPPControl());
        objTypeEPPCDTO.setIdBusiness(typeEPPC.getIdBusiness().getIdBusiness());

        return objTypeEPPCDTO;
    }

    public EntityTypeEPPControl convertToEntityEPP(DTOTypeEPPControl dtoTypeEPPControl, String idBusiness){
        EntityTypeEPPControl entityTypeEPPControl = new EntityTypeEPPControl();
        entityTypeEPPControl.setTypeEPPControl(dtoTypeEPPControl.getTypeEPPControl());
        entityTypeEPPControl.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entityTypeEPPControl;
    }
}
