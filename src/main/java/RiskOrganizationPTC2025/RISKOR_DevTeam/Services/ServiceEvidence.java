package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEvidence;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCloudinary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEvidence;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEvidence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceEvidence {
    @Autowired
    private RepositoryEvidence objRepoE;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ServiceCloudinary cloudinary;

    @Transactional(readOnly = true)
    public List<DTOEvidence> getAllEvidence(String idBusiness){
        List<EntityEvidence> list = objRepoE.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        return list.stream().map(this::convertTOEvidenceDTO).collect(Collectors.toList());
    }

    public DTOEvidence postEvidence(@Valid DTOEvidence dto, String idBusiness){
        if (dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityEvidence saved = objRepoE.save(convertTOEvidenceEntity(dto, idBusiness.toUpperCase()));
        return convertTOEvidenceDTO(saved);
    }

    public DTOEvidence putEvidence(@Valid DTOEvidence dto, String idEvidence, String idBusiness){
        if (dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityEvidence entityEvidence = objRepoE.findByIdEvidenceAndIdBusiness_IdBusiness(idEvidence, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Evidencia no encontrada"));

        if (dto.getAccidentEvidence() != null) entityEvidence.setAccidentEvidence(dto.getAccidentEvidence());
        if (dto.getIdAccident() != null) {
            entityEvidence.setIdAccident(em.getReference(EntityAccident.class, dto.getIdAccident()));
        }

        return convertTOEvidenceDTO(entityEvidence); //Sincroniza por @Transactional
    }

    public boolean deleteEvidence(String idEvidence, String idBusiness){
        if (idEvidence == null || idEvidence.trim().isEmpty()) throw new IllegalArgumentException("El ID de la evidencia no puede ser nulo o vacío");

        long rows = objRepoE.deleteByIdEvidenceAndIdBusiness_IdBusiness(idEvidence, idBusiness.toUpperCase());
        if (rows == 0) throw new EntityNotFoundException("Evidencia no encontrada");
        return true;
    }

    // ===== Conversores (mantengo tus nombres) =====
    private DTOEvidence convertTOEvidenceDTO(EntityEvidence evidence){
        DTOEvidence dto = new DTOEvidence();
        dto.setIdEvidence(evidence.getIdEvidence());
        dto.setAccidentEvidence(evidence.getAccidentEvidence());
        dto.setIdAccident(evidence.getIdAccident() != null ? evidence.getIdAccident().getIdAccident() : null);
        dto.setIdBusiness(evidence.getIdBusiness() != null ? evidence.getIdBusiness().getIdBusiness() : null);
        return dto;
    }

    private EntityEvidence convertTOEvidenceEntity(DTOEvidence dto, String idBusiness){
        EntityEvidence e = new EntityEvidence();
        e.setAccidentEvidence(dto.getAccidentEvidence());
        e.setIdAccident(em.getReference(EntityAccident.class, dto.getIdAccident()));
        e.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
        return e;
    }

    //CRUD DE EVIDENCIAS IMG
    //POST / PUT
    public DTOEvidence updateEvidence(String idBusiness, String idEvidence, MultipartFile image) throws IOException {
        //Verificar que el área pertenece a la empresa
        EntityEvidence evidence = objRepoE.findByIdEvidenceAndIdBusiness_IdBusiness(idEvidence, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia de accidente no encontrada"));

        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Evidences/";
        DTOCloudinary secureUrl = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        evidence.setAccidentEvidence(secureUrl.getUrl());
        return convertTOEvidenceDTO(evidence); //Devolvemos todo en formato JSON
    }

    //Eliminar
    public DTOEvidence deleteEvidenceImage(String idBusiness, String idEvidence) throws IOException {
        EntityEvidence evidence = objRepoE.findByIdEvidenceAndIdBusiness_IdBusiness(idEvidence, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia de accidente no encontrada"));

        String expectedPublicIdWithFolder = "RISKOR/Evidences/" + idBusiness.toUpperCase() + "/" + idEvidence.toUpperCase();

        // 1) Intento con la convención oficial (idArea como public_id)
        cloudinary.deleteByPublicId(expectedPublicIdWithFolder);

        // 2) (fallback) si alguna vez subiste con nombre aleatorio, intenta extraerlo desde la URL
        String url = evidence.getAccidentEvidence();
        if (url != null) {
            String fromUrl = extractPublicIdFromUrl(url); //Ej: RISKOR/areas-sketches/
            if (fromUrl != null && !fromUrl.equalsIgnoreCase(expectedPublicIdWithFolder)) {
                cloudinary.deleteByPublicId(fromUrl);
            }
        }
        evidence.setAccidentEvidence("Sin evidencia"); //Limpiar campo en DB y agregar default
        return convertTOEvidenceDTO(evidence);
    }

    //Método que ayuda a conseguir el ID público que da cloudinary a la img
    private String extractPublicIdFromUrl(String url) {
        try {
            int i = url.indexOf("/upload/");
            if (i < 0) return null;
            String after = url.substring(i + 8); // salta "/upload/"
            if (after.startsWith("v")) { // quita v12345/
                int slash = after.indexOf("/");
                if (slash > 0) after = after.substring(slash + 1);
            }
            int dot = after.lastIndexOf(".");
            if (dot > 0) after = after.substring(0, dot);
            return after; // p.ej. RISKOR/areas-sketches/IDAREA
        } catch (Exception e) {
            return null;
        }
    }
}