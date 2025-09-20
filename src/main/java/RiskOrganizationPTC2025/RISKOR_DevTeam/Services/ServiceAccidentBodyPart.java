package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentBodyPart;
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
public class ServiceAccidentBodyPart {
    @Autowired
    private RepositoryAccidentBodyPart objRepoABP;

    @PersistenceContext
    private EntityManager em;

    //GET (filtrado por empresa)
    @Transactional(readOnly = true)
    public List<DTOAccidentBodyPart> getAllAccidentBodyP(String idBusiness) {
        List<EntityAccidentBodyPart> list = objRepoABP.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return list.stream().map(this::convertToAccidentBPDTO).collect(Collectors.toList());
    }

    //POST
    public DTOAccidentBodyPart postAccidentBodyP(@Valid DTOAccidentBodyPart dto, String idBusiness) {
        if (dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityAccidentBodyPart saved = objRepoABP.save(convertTOAccidentBPEntity(dto, idBusiness.toUpperCase()));
        return convertToAccidentBPDTO(saved);
    }

    //DELETE (seguro y más eficiente por empresa)
    public boolean removeAccidentBodyP(String idAccidentBodyPart, String idBusiness) {
        if (idAccidentBodyPart == null || idAccidentBodyPart.trim().isEmpty()) throw new IllegalArgumentException("El ID no puede ser nulo o vacío");

        long rows = objRepoABP.deleteByIdAccidentBodyPartAndIdBusiness_IdBusiness(idAccidentBodyPart, idBusiness.toUpperCase());
        if (rows == 0) throw new EntityNotFoundException("Registro no encontrado");
        return true;
    }

    private DTOAccidentBodyPart convertToAccidentBPDTO(EntityAccidentBodyPart accidentBP) {
        DTOAccidentBodyPart dto = new DTOAccidentBodyPart();
        dto.setIdAccidentBodyPart(accidentBP.getIdAccidentBodyPart());
        dto.setIdAccident(accidentBP.getIdAccident() != null ? accidentBP.getIdAccident().getIdAccident() : null);
        dto.setIdBodyPart(accidentBP.getIdBodyPart() != null ? accidentBP.getIdBodyPart().getIdBodyPart() : null);
        dto.setIdBusiness(accidentBP.getIdBusiness() != null ? accidentBP.getIdBusiness().getIdBusiness() : null);
        return dto;
    }

    private EntityAccidentBodyPart convertTOAccidentBPEntity(DTOAccidentBodyPart dto, String idBusiness) {
        EntityAccidentBodyPart entityAccidentBodyPart = new EntityAccidentBodyPart();
        entityAccidentBodyPart.setIdAccident(em.getReference(EntityAccident.class, dto.getIdAccident()));
        entityAccidentBodyPart.setIdBodyPart(em.getReference(EntityBodyPart.class, dto.getIdBodyPart()));
        entityAccidentBodyPart.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
        return entityAccidentBodyPart;
    }
}