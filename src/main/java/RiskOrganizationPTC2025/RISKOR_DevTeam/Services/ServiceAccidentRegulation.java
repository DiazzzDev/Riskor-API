package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentRegulation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentRegulation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentRegulation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceAccidentRegulation {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryAccidentRegulation objRepoAR;

    @PersistenceContext
    private EntityManager em;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOAccidentRegulation> getAllAccidentR(String idBusiness){
        List<EntityAccidentRegulation> list = objRepoAR.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return list.stream().map(this::convertTOAccidentRDTO).collect(Collectors.toList());
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOAccidentRegulation postAccidentR(@Valid DTOAccidentRegulation DTOAccidentAR, String idBusiness){
        if (DTOAccidentAR == null) throw new IllegalArgumentException("No pueden haber campos vacíos");
        EntityAccidentRegulation saved = objRepoAR.save(convertTOAccidentREntity(DTOAccidentAR, idBusiness.toUpperCase()));

        return convertTOAccidentRDTO(saved);
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOAccidentRegulation convertTOAccidentRDTO(EntityAccidentRegulation accidentR){
        DTOAccidentRegulation objAccidentRDTO = new DTOAccidentRegulation();
        objAccidentRDTO.setIdAccidentRegulation(accidentR.getIdAccidentRegulation());
        objAccidentRDTO.setIdAccident (accidentR.getIdAccident() != null ? accidentR.getIdAccident().getIdAccident() : null);
        objAccidentRDTO.setIdRegulation(accidentR.getIdRegulation() != null ? accidentR.getIdRegulation().getIdRegulation() : null);
        objAccidentRDTO .setIdBusiness (accidentR.getIdBusiness() != null ? accidentR.getIdBusiness().getIdBusiness() : null);

        return objAccidentRDTO;
    }

    private EntityAccidentRegulation convertTOAccidentREntity(DTOAccidentRegulation dtoAR, String idBusiness){
        EntityAccidentRegulation entityAccidentRegulation = new EntityAccidentRegulation();
        entityAccidentRegulation.setIdAccident  (em.getReference(EntityAccident.class, dtoAR.getIdAccident()));
        entityAccidentRegulation.setIdRegulation(em.getReference(EntityRegulationBusiness.class, dtoAR.getIdRegulation()));
        entityAccidentRegulation.setIdBusiness  (em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entityAccidentRegulation;
    }
}
