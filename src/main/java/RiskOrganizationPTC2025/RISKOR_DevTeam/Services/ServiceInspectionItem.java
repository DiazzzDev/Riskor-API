package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspectionItem;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCloudinary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionItem;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspectionItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
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
public class ServiceInspectionItem {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryInspectionItem objRepoInspectionI;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Autowired
    private ServiceCloudinary cloudinary;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOInspectionItem> getAllInspectionI(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityInspectionItem> loanDetails = objRepoInspectionI.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return loanDetails.map(this::convertTOInspectionIDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOInspectionItem postInspectionI(DTOInspectionItem DTOInspectionItem, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOInspectionItem == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityInspectionItem objeInspectionISaved = objRepoInspectionI.save(convertTOInspectionIEntity(DTOInspectionItem, idBusiness));
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOInspectionIDTO(objeInspectionISaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOInspectionItem putInspectionItem(DTOInspectionItem dtoInspectionItem, String idInspectionItem, String idBusiness) {
        //Validamos que el DTO no venga vacío
        if (dtoInspectionItem == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Buscamos si existe el registro con el ID proporcionado
        EntityInspectionItem inspectionItem = objRepoInspectionI.findByIdInspectionItemAndIdBusiness_IdBusiness(idInspectionItem, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Ítem de inspección no encontrado con ID: " + idInspectionItem));

        //Actualizamos los campos
        inspectionItem.setInspectionTitle(dtoInspectionItem.getInspectionTitle());
        inspectionItem.setInspectionEvidence(dtoInspectionItem.getInspectionEvidence());

        //Retornamos el DTO actualizado
        return convertTOInspectionIDTO(inspectionItem); //Podemos omitir SAVE por uso de @Transactional
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteInspectionItem(String idInspectionItem, String idBusiness){
        if (!objRepoInspectionI.existsByIdInspectionItemAndIdBusiness_IdBusiness(idInspectionItem, idBusiness.toUpperCase())) { return false; }

        objRepoInspectionI.deleteByIdInspectionItemAndIdBusiness_IdBusiness(idInspectionItem, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOInspectionItem convertTOInspectionIDTO(EntityInspectionItem inspectionItem){
        DTOInspectionItem objInspectionIDTO = new DTOInspectionItem();
        objInspectionIDTO.setIdInspectionItem(inspectionItem.getIdInspectionItem());
        objInspectionIDTO.setInspectionTitle(inspectionItem.getInspectionTitle());
        objInspectionIDTO.setInspectionEvidence(inspectionItem.getInspectionEvidence());
        objInspectionIDTO.setIdBusiness(inspectionItem.getIdBusiness().getIdBusiness());

        return objInspectionIDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityInspectionItem convertTOInspectionIEntity(DTOInspectionItem DTOInspectionItem, String idBusiness){
        EntityInspectionItem objEntityInspectionI = new EntityInspectionItem();
        objEntityInspectionI.setInspectionTitle(DTOInspectionItem.getInspectionTitle());
        objEntityInspectionI.setInspectionEvidence(DTOInspectionItem.getInspectionEvidence());
        objEntityInspectionI.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityInspectionI;
    }

    //CRUD DE LA EVIDENCIA DE INSPECCIÓN
    //Post y PUT
    public DTOInspectionItem updateItemEvidence(String idBusiness, String idInspectionItem, MultipartFile image) throws IOException {
        //Verificar que el área pertenece a la empresa
        EntityInspectionItem item = objRepoInspectionI.findByIdInspectionItemAndIdBusiness_IdBusiness(idInspectionItem, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia no encontrada para esta empresa"));

        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Inspections/";
        DTOCloudinary secureUrl = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        item.setInspectionEvidence(secureUrl.getUrl());
        return convertTOInspectionIDTO(item); //Devolvemos todo en formato JSON
    }

    //Eliminar
    public DTOInspectionItem deleteItemEvidence(String idBusiness, String idInspectionItem) throws IOException {
        EntityInspectionItem item = objRepoInspectionI.findByIdInspectionItemAndIdBusiness_IdBusiness(idInspectionItem, idBusiness).orElseThrow(() -> new EntityNotFoundException("Evidencia no encontrada para esta empresa"));

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
        return convertTOInspectionIDTO(item);
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