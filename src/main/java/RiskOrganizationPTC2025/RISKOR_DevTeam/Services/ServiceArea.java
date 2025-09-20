package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCloudinary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryArea;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@Transactional
public class ServiceArea {
    @Autowired
    private RepositoryArea objRepoA;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Autowired
    private ServiceCloudinary cloudinary;

    @Transactional(readOnly = true)
    public Page<DTOArea> getAllAreas(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityArea> permissionPage = objRepoA.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return permissionPage.map(this::convertToDTOA);
    }

    public DTOArea postArea(@Valid DTOArea dtoA, String idBusiness) {
        if(dtoA == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityArea saved = objRepoA.save(convertToEA(dtoA, idBusiness));
        return convertToDTOA(saved);
    }

    public DTOArea putArea(@Valid DTOArea dtoA, String idArea, String idBusiness) {
        if(dtoA == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error
        EntityArea area = objRepoA.findByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness).orElseThrow(() -> new EntityNotFoundException("Área no encontrada para esta empresa"));

        area.setAreaName(dtoA.getAreaName());
        area.setAreaSketch(dtoA.getAreaSketch());

        //EntityArea area = objRepoA.save(areaExist); Ya usamos transactional, al cambiarlo JPA se encarga de actualizar el registro
        return convertToDTOA(area);
    }

    public boolean removeArea(String idArea, String idBusiness){
        if (!objRepoA.existsByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness)) { return false; }

        objRepoA.deleteByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness);
        return true;
    }

    private DTOArea convertToDTOA(EntityArea area){
        DTOArea dtoA = new DTOArea();
        dtoA.setIdArea(area.getIdArea());
        dtoA.setAreaName(area.getAreaName());
        dtoA.setAreaSketch(area.getAreaSketch());
        //Si el objeto idBusiness existe en la entidad area, obtén su ID; si no, simplemente asigna null - Esto por el uso de FETCH LAZY
        dtoA.setIdBusiness(area.getIdBusiness() != null ? area.getIdBusiness().getIdBusiness() : null);

        return dtoA;
    }

    private EntityArea convertToEA(DTOArea dtoA, String idBusiness){
        EntityArea area = new EntityArea();
        area.setAreaName(dtoA.getAreaName());
        area.setAreaSketch(dtoA.getAreaSketch());
        area.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

        return area;
    }

    //CRUD DEL MAPA
    //Post y PUT
    public DTOArea updateAreaSketch(String idBusiness, String idArea, MultipartFile image) throws IOException {
        //Verificar que el área pertenece a la empresa
        EntityArea area = objRepoA.findByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness).orElseThrow(() -> new EntityNotFoundException("Área no encontrada para esta empresa"));

        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Areas-Sketches/";
        DTOCloudinary secureUrl = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        area.setAreaSketch(secureUrl.getUrl());
        return convertToDTOA(area); //Devolvemos todo en formato JSON
    }

    //Eliminar
    public DTOArea deleteAreaSketch(String idBusiness, String idArea) throws IOException {
        EntityArea area = objRepoA.findByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness).orElseThrow(() -> new EntityNotFoundException("Área no encontrada para esta empresa"));

        String expectedPublicIdWithFolder = "RISKOR/areas-sketches/" + idBusiness.toUpperCase() + "/" + idArea.toUpperCase();

        //Se intenta con la convención oficial (idArea como public_id)
        cloudinary.deleteByPublicId(expectedPublicIdWithFolder);

        //Si alguna vez subiste con nombre aleatorio, intenta extraerlo desde la URL
        String url = area.getAreaSketch();
        if (url != null) {
            String fromUrl = extractPublicIdFromUrl(url); // ej: RISKOR/areas-sketches
            if (fromUrl != null && !fromUrl.equalsIgnoreCase(expectedPublicIdWithFolder)) {
                cloudinary.deleteByPublicId(fromUrl);
            }
        }

        area.setAreaSketch("Sin mapa"); //Limpiar campo en DB
        return convertToDTOA(area);
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