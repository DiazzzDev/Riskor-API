package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCloudinary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryRegulationBusiness;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class ServiceRegulationBusiness {
    @Autowired
    private RepositoryRegulationBusiness objRepoRB;

    @Autowired
    private ServiceCloudinary cloudinary;

    @Autowired
    private RepositoryArea objRepoA; //Se manda a llamar para validar que un área pertence a una empresa correcta

    @PersistenceContext
    private EntityManager em; //Ayuda a evitar cargar objetos completos en FK

    //Método para validar si una empresa pertenece a una empresa
    private void validateAreaBelongsToBusiness(String idArea, String idBusiness) {
        if (!objRepoA.existsByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness)) { //Llamamos un método del repo de área que devuelve booleano según una restricción
            throw new IllegalArgumentException("El área no pertenece a la empresa indicada");
        }
    }

    public Page<DTORegulationBusiness> getRegulations(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityRegulationBusiness> regulationBusinessPage = objRepoRB.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return regulationBusinessPage.map(this::convertToDTORB);
    }

    //POST Principal al crear una regulación empresarial
    //Haremos uso de transactional con rollback en caso de que un error suceda y no quede un REGISTRO FLOTANTE
    @Transactional(rollbackFor = Exception.class)
    public DTORegulationBusiness postRegulationBusiness(@Valid DTORegulationBusiness dto, MultipartFile file, String idBusiness) {
        DTOCloudinary up = null;
        try{
            if (dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");
            if (file == null || file.isEmpty()) throw new IllegalArgumentException("Documento pendiente");

            //Ligeras validaciones
            dto.setIdBusiness(idBusiness); //Asignamos desde antes el negocio - Evitamos que el cliente elija en que negocio registrar
            validateAreaBelongsToBusiness(dto.getIdArea(), idBusiness); //Validamos que el área que se va a registrar corresponda a la empresa

            up = cloudinary.uploadImage(file, "RISKOR/Regulations-Documents/");
            dto.setRegulationDocument(up.getUrl());                 // guardar URL en la entidad

            EntityRegulationBusiness saved = objRepoRB.save(convertToERB(dto));

            return convertToDTORB(saved);
        } catch (RuntimeException | IOException e) {
            // si ya subimos la imagen, borrarla para no dejar basura
            if (up != null && up.getPublicId() != null) {
                try {
                    cloudinary.deleteByPublicId(up.getPublicId());
                } catch (Exception ignore) {
                }
            }
            return null;
        }
    }

    public DTORegulationBusiness putRegulationBusiness(@Valid DTORegulationBusiness dtoRB, String idRegulation, String idBusiness) {
        if (dtoRB == null){ throw new IllegalArgumentException("No pueden haber campos vacios");}

        EntityRegulationBusiness regulation = objRepoRB.findByIdRegulationAndIdBusiness_IdBusiness(idRegulation, idBusiness).orElseThrow(() -> new EntityNotFoundException("Regulación no encontrada con ID: " + idRegulation));

        //Verifica que el área del DTO pertenezca a la empresa del path
        validateAreaBelongsToBusiness(dtoRB.getIdArea(), idBusiness);

        regulation.setRegulationTitle(dtoRB.getRegulationTitle());
        regulation.setRegulationDescription(dtoRB.getRegulationDescription());
        regulation.setCreationDate(dtoRB.getCreationDate());
        regulation.setRegulationDocument(dtoRB.getRegulationDocument());


        regulation.setIdRiskStatus(em.getReference(EntityRiskStatus.class, dtoRB.getIdRiskStatus()));
        regulation.setIdRegulationCategory(em.getReference(EntityRegulationCategory.class, dtoRB.getIdRegulationCategory()));
        regulation.setIdArea(em.getReference(EntityArea.class, dtoRB.getIdArea()));
        regulation.setIdRiskLevel(em.getReference(EntityRiskLevel.class, dtoRB.getIdRiskLevel()));

        //EntityRegulationBusiness regulation = objRepoRB.save(regulation); Ya no se necesita porque se usa transactional
        return convertToDTORB(regulation);
    }

    public boolean removeRegulationBusiness(String idRegulation, String idBusiness){
        if (idRegulation == null || idRegulation.trim().isEmpty()) return false;

        boolean exists = objRepoRB.existsByIdRegulationAndIdBusiness_IdBusiness(idRegulation, idBusiness);
        if (!exists) { return false; }

        objRepoRB.deleteByIdRegulationAndIdBusiness_IdBusiness(idRegulation, idBusiness);
        return true;
    }

    private DTORegulationBusiness convertToDTORB(EntityRegulationBusiness eRB){
        DTORegulationBusiness dtoRB = new DTORegulationBusiness();
        dtoRB.setIdRegulation(eRB.getIdRegulation());
        dtoRB.setRegulationTitle(eRB.getRegulationTitle());
        dtoRB.setRegulationDescription(eRB.getRegulationDescription());
        dtoRB.setCreationDate(eRB.getCreationDate());
        dtoRB.setRegulationDocument(eRB.getRegulationDocument());

        //IDs de FKs (evita cargar objetos completos por LAZY)
        dtoRB.setIdRiskStatus(eRB.getIdRiskStatus() != null ? eRB.getIdRiskStatus().getIdRiskStatus() : null);
        dtoRB.setIdRegulationCategory(eRB.getIdRegulationCategory() != null ? eRB.getIdRegulationCategory().getIdRegulationCategory() : null);
        dtoRB.setIdArea(eRB.getIdArea() != null ? eRB.getIdArea().getIdArea() : null);
        dtoRB.setIdRiskLevel(eRB.getIdRiskLevel() != null ? eRB.getIdRiskLevel().getIdRiskLevel() : null);
        dtoRB.setIdBusiness(eRB.getIdBusiness() != null ? eRB.getIdBusiness().getIdBusiness() : null);
        return dtoRB;
    }

    private EntityRegulationBusiness convertToERB(DTORegulationBusiness dtoRB){
        EntityRegulationBusiness regulationBusiness = new EntityRegulationBusiness();
        regulationBusiness.setRegulationTitle(dtoRB.getRegulationTitle());
        regulationBusiness.setRegulationDescription(dtoRB.getRegulationDescription());
        regulationBusiness.setCreationDate(dtoRB.getCreationDate());
        regulationBusiness.setRegulationDocument(dtoRB.getRegulationDocument());

        //FKs que se les hace referencia con EntityManager, no se hace SELECT
        regulationBusiness.setIdRiskStatus(em.getReference(EntityRiskStatus.class, dtoRB.getIdRiskStatus()));
        regulationBusiness.setIdRegulationCategory(em.getReference(EntityRegulationCategory.class, dtoRB.getIdRegulationCategory()));
        regulationBusiness.setIdArea(em.getReference(EntityArea.class, dtoRB.getIdArea()));
        regulationBusiness.setIdRiskLevel(em.getReference(EntityRiskLevel.class, dtoRB.getIdRiskLevel()));
        regulationBusiness.setIdBusiness(em.getReference(EntityBusinessInfo.class, dtoRB.getIdBusiness()));
        return regulationBusiness;
    }

    //Post y PUT
    public DTORegulationBusiness updateRegulation(String idBusiness, String idRegulation, MultipartFile image) throws IOException {
        //Verificar que el área pertenece a la empresa
        EntityRegulationBusiness reg = objRepoRB.findByIdRegulationAndIdBusiness_IdBusiness(idRegulation, idBusiness).orElseThrow(() -> new EntityNotFoundException("Regulación no encontrada para esta empresa"));

        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Regulations-Documents/";
        DTOCloudinary secureUrl = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        reg.setRegulationDocument(secureUrl.getUrl());
        return convertToDTORB(reg); //Devolvemos todo en formato JSON
    }

    //Eliminar
    public DTORegulationBusiness deleteDocument(String idBusiness, String idRegulation) throws IOException {
        EntityRegulationBusiness reg = objRepoRB.findByIdRegulationAndIdBusiness_IdBusiness(idRegulation, idBusiness).orElseThrow(() -> new EntityNotFoundException("Regulación no encontrada para esta empresa"));

        String expectedPublicIdWithFolder = "RISKOR/Regulations-Documents/" + idBusiness.toUpperCase() + "/" + idRegulation.toUpperCase();

        //Se intenta con la convención oficial (idArea como public_id)
        cloudinary.deleteByPublicId(expectedPublicIdWithFolder);

        //Si alguna vez subiste con nombre aleatorio, intenta extraerlo desde la URL
        String url = reg.getRegulationDocument();
        if (url != null) {
            String fromUrl = extractPublicIdFromUrl(url); // ej: RISKOR/areas-sketches
            if (fromUrl != null && !fromUrl.equalsIgnoreCase(expectedPublicIdWithFolder)) {
                cloudinary.deleteByPublicId(fromUrl);
            }
        }

        reg.setRegulationDocument("Sin documento"); //Limpiar campo en DB
        return convertToDTORB(reg);
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
