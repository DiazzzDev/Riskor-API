package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityLocation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOLocation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryLocation;
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
public class ServiceLocation {
    @Autowired
    private RepositoryLocation objRepoL;

    @PersistenceContext
    private EntityManager em; //Ayuda a evitar cargar objetos completos en FK

    @Transactional(readOnly = true)
    public List<DTOLocation> getLocation(String idBusiness){
        List<EntityLocation> locations = objRepoL.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return locations.stream().map(this::convertToDTOL).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<DTOLocation> getLocationByName(String idBusiness, String name, String idArea, int page, int size) {
        if (page < 0 || size <= 0) throw new IllegalArgumentException("page y size inválidos");

        Pageable pageable = PageRequest.of(page, size);
        String safeName = (name == null) ? "" : name.trim();
        String safeBusiness = idBusiness.trim(); // no forzar toUpperCase

        Page<EntityLocation> pageEntities;
        if (idArea != null && !idArea.isBlank()) {
            // Buscar dentro de un area específica + business
            pageEntities = objRepoL.findByLocationNameContainingIgnoreCaseAndIdArea_IdAreaAndIdBusiness_IdBusiness(safeName, idArea.trim(), safeBusiness, pageable);
        } else {
            // Buscar por nombre dentro de la empresa
            pageEntities = objRepoL.findByLocationNameContainingIgnoreCaseAndIdBusiness_IdBusiness(safeName, safeBusiness, pageable);
        }

        return pageEntities.map(this::convertToDTOL);
    }

    public DTOLocation postLocation(@Valid DTOLocation dtoL, String idBusiness){
        if (dtoL == null){ throw new IllegalArgumentException("No pueden haber campos vacíos"); }

        EntityLocation saved = objRepoL.save(convertToEL(dtoL, idBusiness));
        return convertToDTOL(saved);
    }

    public DTOLocation putLocation(@Valid DTOLocation dtoL, String idLocation, String idBusiness) {
        if (dtoL == null) { throw new IllegalArgumentException("No puede haber campos vacíos"); }

        EntityLocation location = objRepoL.findByIdLocationAndIdBusiness_IdBusiness(idLocation, idBusiness).orElseThrow(() -> new IllegalArgumentException("No se encontró la ubicación para esta empresa"));

        location.setLocationName(dtoL.getLocationName());
        location.setIdArea(em.getReference(EntityArea.class, dtoL.getIdArea())); //Se hace esto porque esta clase es una FK y tiene lazy fetch

        //EntityLocation location = objRepoL.save(locationExists); Ya no se necesita por uso de @Transactional
        return convertToDTOL(location);
    }

    public boolean removeLocation(String idLocation, String idBusiness) {
        if (idLocation == null || idLocation.trim().isEmpty()) { throw new IllegalArgumentException("El ID del negocio no puede ser nulo o vacío"); }

        boolean exists = objRepoL.existsByIdLocationAndIdBusiness_IdBusiness(idLocation, idBusiness);
        if (!exists){ throw new EntityNotFoundException("No se encontró la ubicación para esta empresa"); }

        objRepoL.deleteByIdLocationAndIdBusiness_IdBusiness(idLocation, idBusiness);
        return true;
    }

    private DTOLocation convertToDTOL(EntityLocation location){
        DTOLocation dtoL = new DTOLocation();
        dtoL.setIdLocation(location.getIdLocation());
        dtoL.setLocationName(location.getLocationName());
        dtoL.setIdArea(location.getIdArea() != null ? location.getIdArea().getIdArea() : null);
        dtoL.setIdBusiness(location.getIdBusiness() != null ? location.getIdBusiness().getIdBusiness() : null);
        return dtoL;
    }

    private EntityLocation convertToEL(DTOLocation dtoL, String idBusiness){
        EntityLocation location = new EntityLocation();
        location.setLocationName(dtoL.getLocationName());
        location.setIdArea(em.getReference(EntityArea.class, dtoL.getIdArea()));
        location.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
        return location;
    }
}
