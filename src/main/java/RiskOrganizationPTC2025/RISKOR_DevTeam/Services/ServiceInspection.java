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
    private RepositoryInspection objRepoI;

    @Autowired
    private RepositoryInspectionStatus repoStatus;

    @Autowired
    private ServiceCloudinary cloudinary;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Transactional(readOnly = true)
    public DTOInspection getInspectionById(String idBusiness, String idInspection) {
        EntityInspection inspection = objRepoI.findByIdInspectionAndIdBusiness_IdBusiness(idInspection.toUpperCase(), idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Inspección no encontrada con ID: " + idInspection));
        return convertTOInspectionDTO(inspection);
    }

    @Transactional(readOnly = true)
    public Page<DTOInspection> getInspectionsByTitle(int page, int size, String title, String idBusiness) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("El título es requerido");

        Pageable pageable = PageRequest.of(page, size);
        Page<EntityInspection> inspections = objRepoI.findByInspectionTitleContainingIgnoreCaseAndIdBusiness_IdBusiness(title.trim(), idBusiness.toUpperCase(), pageable);
        return inspections.map(this::convertTOInspectionDTO);
    }

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOInspection> getAllInspection(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityInspection> inspections = objRepoI.findByIdBusiness_IdBusiness(idBusiness, pageable);
        return inspections.map(this::convertTOInspectionDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOInspection postInspection(@Valid DTOInspection dtoInspection, String idBusiness, String idEmployee, MultipartFile file){
        DTOCloudinary up = null; //Limpieza en caso falla luego
        try {
            //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
            if (dtoInspection == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

            if(file != null){
                up = cloudinary.uploadImage(file, "RISKOR/Inspections/");
                dtoInspection.setInspectionEvidence(up.getUrl());                 // guardar URL de la img
            }else {
                dtoInspection.setInspectionEvidence("Sin evidencia");
            }

            //Caso contrario, se procede con la inserción de datos (POST)
            EntityInspection saved = objRepoI.save(convertTOInspectionEntity(dtoInspection, idBusiness, idEmployee));
            return convertTOInspectionDTO(saved); //Retornamos los valores en formato JSON con 201 CREATED
        } catch (Exception e) {
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

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOInspection putInspection(@Valid DTOInspection dtoInspection, String idInspection, String idBusiness, MultipartFile file) throws IOException{
        DTOCloudinary up = null;
        try {
            //Validamos que el DTO no venga vacío
            if (dtoInspection == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

            //Buscamos si existe el registro con el ID proporcionado
            EntityInspection inspection = objRepoI.findByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Inspección no encontrada con ID: " + idInspection));

            //Actualizamos los campos
            inspection.setInspectionTitle(dtoInspection.getInspectionTitle());
            inspection.setObservation(dtoInspection.getObservation());
            inspection.setInspectionDate(dtoInspection.getInspectionDate());

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

            //Evidencia: solo si mandan un archivo nuevo
            if (file != null && !file.isEmpty()) {
                String oldUrl = inspection.getInspectionEvidence();

                up = cloudinary.uploadImage(file, "RISKOR/Inspections/");
                inspection.setInspectionEvidence(up.getUrl()); // ¡NO sobreescribir con dto!

                // Limpieza del anterior (best-effort)
                try {
                    if (oldUrl != null && !oldUrl.trim().isEmpty() && !"Sin evidencia".equalsIgnoreCase(oldUrl)) {
                        cloudinary.deleteByUrl(oldUrl);
                    }
                } catch (Exception ignore) {}
            } else {
                if (dtoInspection.getInspectionEvidence() != null && !dtoInspection.getInspectionEvidence().trim().isEmpty()) {
                    inspection.setInspectionEvidence(dtoInspection.getInspectionEvidence());
                }
            }

            //Retornamos el DTO actualizado
            return convertTOInspectionDTO(inspection);
        } catch (RuntimeException | IOException e) {
            // si se subió archivo nuevo y falló luego, limpiar en Cloudinary
            if (up != null && up.getPublicId() != null) {
                try { cloudinary.deleteByPublicId(up.getPublicId()); } catch (Exception ignore) {}
            }
            throw e;
        }
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteInspection(String idInspection, String idBusiness){
        if (idInspection == null || idInspection.trim().isEmpty()) return false;

        var inspection = objRepoI.findByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness).orElseThrow(() -> new EntityNotFoundException("Inspección no encontrada con ID: " + idInspection));
        objRepoI.deleteByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness.toUpperCase());

        //Intenta borrar el archivo en Cloudinary
        try {
            //Vamos a aplicar una condición que verifica si la evidencia es diferente de null, y no se aplicará si dice "Sin evidencia"
            if (inspection.getInspectionEvidence() != null && !inspection.getInspectionEvidence().isBlank() && !"Sin evidencia".equalsIgnoreCase(inspection.getInspectionEvidence())) {
                cloudinary.deleteByUrl(inspection.getInspectionEvidence());
            }
        } catch (Exception ignore) {}

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
        dtoInspection.setAreaName(inspection.getIdArea().getAreaName());

        dtoInspection.setIdInspectionType(inspection.getIdInspectionType().getIdInspectionType());
        dtoInspection.setInspectionType(inspection.getIdInspectionType().getInspectionType());

        dtoInspection.setIdInspectionStatus(inspection.getIdInspectionStatus().getIdInspectionStatus());
        dtoInspection.setInspectionStatus(inspection.getIdInspectionStatus().getInspectionStatus());

        dtoInspection.setIdBusiness(inspection.getIdBusiness().getIdBusiness());

        return dtoInspection;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityInspection convertTOInspectionEntity(DTOInspection dtoInspection, String idBusiness, String idEmployee){
        EntityInspection entityInspection = new EntityInspection();
        entityInspection.setInspectionTitle(dtoInspection.getInspectionTitle());
        entityInspection.setInspectionDate(LocalDate.now());
        entityInspection.setInspectionEvidence(dtoInspection.getInspectionEvidence());
        entityInspection.setObservation(dtoInspection.getObservation());
        //FKs
        //Asigna desde acá el empleado que fue encargado de la inspección a partir del id de la cookie (Es el id del empleado)
        entityInspection.setIdEmployee(em.getReference(EntityEmployee.class, idEmployee));
        entityInspection.setIdArea(em.getReference(EntityArea.class, dtoInspection.getIdArea()));
        entityInspection.setIdInspectionType(em.getReference(EntityInspectionType.class, dtoInspection.getIdInspectionType()));

        //Por defecto asignamos el status como pendiente
        String pendingStatusId = repoStatus.findPendingId("PENDIENTE").orElseThrow(() -> new EntityNotFoundException("El estado 'PENDIENTE' no fue encontrado en la base de datos."));

        entityInspection.setIdInspectionStatus(em.getReference(EntityInspectionStatus.class, pendingStatusId));
        entityInspection.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return entityInspection;
    }

    //Eliminar
    public DTOInspection deleteEvidence(String idBusiness, String idInspection) throws IOException {
        EntityInspection item = objRepoI.findByIdInspectionAndIdBusiness_IdBusiness(idInspection, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia no encontrada para esta empresa"));

        String expectedPublicIdWithFolder = "RISKOR/Inspections/" + idBusiness.toUpperCase() + "/" + idInspection.toUpperCase();

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