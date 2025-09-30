package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCloudinary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspectionStatus;
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
import java.time.LocalDate;

@Service
@Transactional
public class ServiceInspection {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryInspection objRepoInspection;

    @Autowired
    private RepositoryInspectionStatus repoStatus;

    @Autowired
    private ServiceCloudinary cloudinary;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    public DTOInspection getInspectionById(String idBusiness, String idInspection) {
        EntityInspection inspection = objRepoInspection.findByIdInspectionAndIdBusiness_IdBusiness(idInspection.toUpperCase(), idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Inspección no encontrada con ID: " + idInspection));
        return convertTOInspectionDTO(inspection);
    }

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOInspection> getAllInspection(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityInspection> inspections = objRepoInspection.findByIdBusiness_IdBusiness(idBusiness, pageable);
        return inspections.map(this::convertTOInspectionDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOInspection postInspection(@Valid DTOInspection dtoInspection, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (dtoInspection == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityInspection objeInspectionSaved = objRepoInspection.save(convertTOInspectionEntity(dtoInspection, idBusiness));
        return convertTOInspectionDTO(objeInspectionSaved); //Finalmente, retornamos los valores en formato JSON con 201 CREATED
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOInspection putInspection(@Valid DTOInspection dtoInspection, String idInspection, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoInspection == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Buscamos si existe el registro con el ID proporcionado
        EntityInspection inspection = objRepoInspection.findByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Inspección no encontrada con ID: " + idInspection));

        //Actualizamos los campos
        inspection.setInspectionTitle(dtoInspection.getInspectionTitle());
        inspection.setInspectionEvidence(dtoInspection.getInspectionEvidence());
        inspection.setObservation(dtoInspection.getObservation());

        //Si las FKs no son modificadas en el PUT se mantendrán en su valor original
        if (dtoInspection.getIdArea() != null) {
            inspection.setIdArea(em.getReference(EntityArea.class, dtoInspection.getIdArea()));
        }
        if (dtoInspection.getIdInspectionType() != null) {
            inspection.setIdInspectionType(em.getReference(EntityInspectionType.class, dtoInspection.getIdInspectionType()));
        }
        if (dtoInspection.getIdInspectionStatus() != null) {
            inspection.setIdInspectionStatus(em.getReference(EntityInspectionStatus.class, dtoInspection.getIdInspectionStatus()));
        }

        //Retornamos el DTO actualizado
        return convertTOInspectionDTO(inspection);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteInspection(String idInspection, String idBusiness){
        if (!objRepoInspection.existsByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase())) { return false; }

        objRepoInspection.deleteByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOInspection convertTOInspectionDTO(EntityInspection inspection){
        DTOInspection dtoInspection = new DTOInspection();
        dtoInspection.setIdInspection(inspection.getIdInspection());
        dtoInspection.setInspectionTitle(inspection.getInspectionTitle());
        dtoInspection.setInspectionEvidence(inspection.getInspectionEvidence());
        dtoInspection.setInspectionDate(inspection.getInspectionDate());
        dtoInspection.setObservation(inspection.getObservation());

        dtoInspection.setIdEmployee(inspection.getIdEmployee().getIdEmployee());
        dtoInspection.setFirstName(inspection.getIdEmployee().getFirstName());
        dtoInspection.setLastName(inspection.getIdEmployee().getLastName());

        dtoInspection.setIdArea(inspection.getIdArea().getIdArea());
        dtoInspection.setIdArea(inspection.getIdArea().getAreaName());

        dtoInspection.setIdInspectionType(inspection.getIdInspectionType().getIdInspectionType());
        dtoInspection.setInspectionType(inspection.getIdInspectionType().getInspectionType());

        dtoInspection.setIdInspectionStatus(inspection.getIdInspectionStatus().getIdInspectionStatus());
        dtoInspection.setInspectionStatus(inspection.getIdInspectionStatus().getInspectionStatus());

        dtoInspection.setIdBusiness(inspection.getIdBusiness().getIdBusiness());

        return dtoInspection;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityInspection convertTOInspectionEntity(DTOInspection dtoInspection, String idBusiness){
        EntityInspection entityInspection = new EntityInspection();
        entityInspection.setInspectionTitle(dtoInspection.getInspectionTitle());
        entityInspection.setInspectionEvidence(dtoInspection.getInspectionEvidence());
        entityInspection.setInspectionDate(LocalDate.now());
        entityInspection.setObservation(dtoInspection.getObservation());
        entityInspection.setIdEmployee(em.getReference(EntityEmployee.class, dtoInspection.getIdEmployee()));
        entityInspection.setIdArea(em.getReference(EntityArea.class, dtoInspection.getIdArea()));
        entityInspection.setIdInspectionType(em.getReference(EntityInspectionType.class, dtoInspection.getIdInspectionType()));

        String pendingStatusId = repoStatus.findPendingId("PENDIENTE").orElseThrow(() -> new EntityNotFoundException("El estado 'PENDIENTE' no fue encontrado en la base de datos."));
        entityInspection.setIdInspectionStatus(em.getReference(EntityInspectionStatus.class, pendingStatusId));
        entityInspection.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entityInspection;
    }

    //CRUD DE LA EVIDENCIA DE INSPECCIÓN
    //Post y PUT
    public DTOInspection updateItemEvidence(String idBusiness, String idInspectionItem, MultipartFile image) throws IOException {
        //Verificar que el área pertenece a la empresa
        EntityInspection item = objRepoInspection.findByIdInspectionAndIdBusiness_IdBusiness(idInspectionItem, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia no encontrada para esta empresa"));

        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Inspections/";
        DTOCloudinary secureUrl = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        item.setInspectionEvidence(secureUrl.getUrl());
        return convertTOInspectionDTO(item); //Devolvemos todo en formato JSON
    }

    //Eliminar
    public DTOInspection deleteItemEvidence(String idBusiness, String idInspectionItem) throws IOException {
        EntityInspection item = objRepoInspection.findByIdInspectionAndIdBusiness_IdBusiness(idInspectionItem, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia no encontrada para esta empresa"));

        String expectedPublicIdWithFolder = "RISKOR/Inspections/" + idBusiness.toUpperCase() + "/" + idInspectionItem.toUpperCase();

        //Se intenta con la convención oficial (idEvidence como public_id)
        cloudinary.deleteByPublicId(expectedPublicIdWithFolder);

        //Si alguna vez subiste con nombre aleatorio, intenta extraerlo desde la URL
        String url = item.getInspectionEvidence();
        if (url != null) {
            String fromUrl = extractPublicIdFromUrl(url); // ej: RISKOR/areas-sketches/{biz}/{algo}
            if (fromUrl != null && !fromUrl.equalsIgnoreCase(expectedPublicIdWithFolder)) {
                cloudinary.deleteByPublicId(fromUrl);
            }
        }

        item.setInspectionEvidence("Sin evidencia"); //Limpiar campo en DB
        return convertTOInspectionDTO(item);
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