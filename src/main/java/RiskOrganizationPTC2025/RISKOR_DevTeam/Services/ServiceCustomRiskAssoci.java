package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityCustomRiskAssoci;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityCustomTitleInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCustomRiskAssoci;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryCustomRiskAssoci;
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
public class ServiceCustomRiskAssoci {
    @Autowired
    private RepositoryCustomRiskAssoci objRepoCRA;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Transactional(readOnly = true)
    public List<DTOCustomRiskAssoci> getAllRisks(String idBusiness){
        List<EntityCustomRiskAssoci> titleInspectionList = objRepoCRA.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return titleInspectionList.stream().map(this::convertToDTOCTI).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DTOCustomRiskAssoci> getAllRisksByTitle(String idBusiness, String idCustomTitleInsp){
        List<EntityCustomRiskAssoci> titleInspectionList = objRepoCRA.findByIdBusiness_IdBusinessAndIdCustomTitleInsp_IdCustomTitleInsp(idBusiness.toUpperCase(), idCustomTitleInsp.toUpperCase());
        return titleInspectionList.stream().map(this::convertToDTOCTI).collect(Collectors.toList());
    }

    public DTOCustomRiskAssoci postRisk(@Valid DTOCustomRiskAssoci dto, String idBusiness) {
        if(dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        if (dto.getIdCustomTitleInsp() == null || dto.getIdCustomTitleInsp().isBlank()) {
            throw new IllegalArgumentException("título de inspección es obligatorio");
        }

        EntityCustomRiskAssoci saved = objRepoCRA.save(convertToEntityCTI(dto, idBusiness));
        return convertToDTOCTI(saved);
    }

    public DTOCustomRiskAssoci patchRisk(@Valid DTOCustomRiskAssoci dto, String idCustomRiskAssoci, String idBusiness) {
        if(dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error
        EntityCustomRiskAssoci risk = objRepoCRA.findByIdCustomRiskAssociAndIdBusiness_IdBusiness(idCustomRiskAssoci, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Riesgo no encontradio para esta empresa"));

        //Solo se va a permitir cambiar de nombre del riesgo, más no de donde pertenece
        risk.setCustomRiskAssoci(dto.getCustomRiskAssoci());

        return convertToDTOCTI(risk);
    }

    public boolean removeRisk(String idCustomRiskAssoci, String idBusiness){
        if (!objRepoCRA.existsByIdCustomRiskAssociAndIdBusiness_IdBusiness(idCustomRiskAssoci, idBusiness.toUpperCase())) { return false; }

        objRepoCRA.deleteByIdCustomRiskAssociAndIdBusiness_IdBusiness(idCustomRiskAssoci, idBusiness.toUpperCase());
        return true;
    }

    private DTOCustomRiskAssoci convertToDTOCTI(EntityCustomRiskAssoci entityCustomRiskAssoci){
        DTOCustomRiskAssoci dto = new DTOCustomRiskAssoci();
        dto.setIdCustomRiskAssoci(entityCustomRiskAssoci.getIdCustomRiskAssoci());
        dto.setCustomRiskAssoci(entityCustomRiskAssoci.getCustomRiskAssoci());
        dto.setIdCustomTitleInsp(entityCustomRiskAssoci.getIdCustomTitleInsp().getIdCustomTitleInsp());
        dto.setIdBusiness(entityCustomRiskAssoci.getIdBusiness().getIdBusiness());

        return dto;
    }

    public EntityCustomRiskAssoci convertToEntityCTI(DTOCustomRiskAssoci dtoCustomRiskAssoci, String idBusiness){
        EntityCustomRiskAssoci entity = new EntityCustomRiskAssoci();
        entity.setCustomRiskAssoci(dtoCustomRiskAssoci.getCustomRiskAssoci());
        entity.setIdCustomTitleInsp(em.getReference(EntityCustomTitleInspection.class, dtoCustomRiskAssoci.getIdCustomTitleInsp()));
        entity.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entity;
    }
}
