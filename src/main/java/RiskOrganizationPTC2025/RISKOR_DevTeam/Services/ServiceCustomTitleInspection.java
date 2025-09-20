package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityCustomTitleInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCustomTitleInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryCustomTitleInspection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceCustomTitleInspection {
    @Autowired
    private RepositoryCustomTitleInspection objRepoCTI;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Transactional(readOnly = true)
    public List<DTOCustomTitleInspection> getAllTitles(String idBusiness){
        List<EntityCustomTitleInspection> titleInspectionList = objRepoCTI.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return titleInspectionList.stream().map(this::convertToDTOCTI).collect(Collectors.toList());
    }

    public DTOCustomTitleInspection postTitle(@Valid DTOCustomTitleInspection dto, String idBusiness) {
        if(dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityCustomTitleInspection saved = objRepoCTI.save(convertToEntityCTI(dto, idBusiness));
        return convertToDTOCTI(saved);
    }

    public DTOCustomTitleInspection patchTitle(@Valid DTOCustomTitleInspection dto, String idArea, String idBusiness) {
        if(dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error
        EntityCustomTitleInspection title = objRepoCTI.findByIdCustomTitleInspAndIdBusiness_IdBusiness(idArea, idBusiness).orElseThrow(() -> new EntityNotFoundException("Título no encontradio para esta empresa"));

        title.setCustomTitleInsp(dto.getCustomTitleInsp());

        return convertToDTOCTI(title);
    }

    public boolean removeTitle(String idArea, String idBusiness){
        if (!objRepoCTI.existsByIdCustomTitleInspAndIdBusiness_IdBusiness(idArea, idBusiness)) { return false; }

        objRepoCTI.deleteByIdCustomTitleInspAndIdBusiness_IdBusiness(idArea, idBusiness);
        return true;
    }

    private DTOCustomTitleInspection convertToDTOCTI(EntityCustomTitleInspection entityCustomTitleInspection){
        DTOCustomTitleInspection dto = new DTOCustomTitleInspection();
        dto.setIdCustomTitleInsp(entityCustomTitleInspection.getIdCustomTitleInsp());
        dto.setCustomTitleInsp(entityCustomTitleInspection.getCustomTitleInsp());
        dto.setIdBusiness(entityCustomTitleInspection.getIdBusiness().getIdBusiness());

        return dto;
    }

    public EntityCustomTitleInspection convertToEntityCTI(DTOCustomTitleInspection dtoCustomTitleInspection, String idBusiness){
        EntityCustomTitleInspection entity = new EntityCustomTitleInspection();
        entity.setCustomTitleInsp(dtoCustomTitleInspection.getCustomTitleInsp());
        entity.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entity;
    }
}
