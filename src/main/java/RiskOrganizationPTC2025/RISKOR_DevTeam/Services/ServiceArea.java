package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAreaEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryLocation;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ServiceArea {
    @Autowired
    private RepositoryArea objRepoA;

    @Autowired
    private RepositoryLocation objRepoL;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Autowired
    private ServiceCloudinary cloudinary;

    //Clase e interfaz inyectadas para el POST de área/locación-area/empleados-area
    @Autowired
    private ServiceEmployee objServiceE; //Para devolver el DTOEmployee

    @Autowired
    private RepositoryAreaEmployee objRepoAE;

    @Transactional(readOnly = true)
    public DTOArea getAreaById(String idBusiness, String idArea) {
        EntityArea area = objRepoA.findByIdAreaAndIdBusiness_IdBusiness(idArea, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Área no encontrada"));
        return convertToDTOA(area);
    }

    @Transactional(readOnly = true)
    public Page<DTOArea> getAllAreas(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityArea> permissionPage = objRepoA.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return permissionPage.map(this::convertToDTOA);
    }

    //Este post es para un área que se va a crear, NO para agregar empleados a esta área si ya está creada
    //Para realizar un post de muchos empleados a un área ya asignada hay un endpoint en ControllerAreaEmployee
    public DTOAreaInBusiness postAreaBundle(String idBusiness, DTOAreaBundleRequest req, MultipartFile image) {
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("El mapa del área es obligatorio");
        //Crear área (Manda a llamar un método para crear un área)
        DTOArea createdArea = this.postArea(req.getArea(), idBusiness, image);

        //Crear locaciones en el área
        List<DTOLocation> createdLocations = new ArrayList<>();
        //Si el JSON no viene nulo ni vació en las locaciones...
        if (req.getLocations() != null && !req.getLocations().isEmpty()) {
            //Por cada locación de tipo DTOLocationCreate en el JSON recibido..
            for (DTOLocationCreate lc : req.getLocations()) {
                //Vamos a guardar la locación agregando el id del área recién creada con el nombre enviado
                EntityLocation entity = toEntityLocation(idBusiness, createdArea.getIdArea(), lc);
                EntityLocation saved = objRepoL.save(entity);

                //Se hace map a DTOLocation y se guardan en la lista creada
                DTOLocation dtoOut = new DTOLocation();
                dtoOut.setIdLocation(saved.getIdLocation());
                dtoOut.setLocationName(saved.getLocationName());
                dtoOut.setIdArea(createdArea.getIdArea());
                dtoOut.setIdBusiness(idBusiness);
                createdLocations.add(dtoOut);
            }
        }

        //Asignar empleados al área
        List<DTOEmployee> assigned = Collections.emptyList();
        if (req.getEmployeeIds() != null && !req.getEmployeeIds().isEmpty()) {
            assigned = assignEmployeesToArea(idBusiness, createdArea.getIdArea(), req.getEmployeeIds());
        }

        //Armar respuesta final: El área registrada con todas las locaciones hechas en el post y todos los empleados enviados
        DTOAreaInBusiness out = new DTOAreaInBusiness();
        out.setArea(createdArea);
        out.setLocationsInArea(createdLocations);
        out.setEmployeesOnArea(assigned);
        return out;
    }

    //Método para subir el área para luego guardar las locaciones y empleados que pertenecen a esa misma
    public DTOArea postArea(@Valid DTOArea dtoA, String idBusiness, MultipartFile image) {
        if(dtoA == null) throw new IllegalArgumentException("No pueden haber campos vacíos");
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("El mapa del área es obligatorio");

        DTOCloudinary up = null;
        try {
            // sube a Cloudinary (usa una carpeta por empresa si quieres)
            String folder = "RISKOR/Areas-Sketches/" + idBusiness.toUpperCase() + "/";
            up = cloudinary.uploadImage(image, folder);

            // construir entidad usando la URL subida
            EntityArea area = new EntityArea();
            area.setAreaName(dtoA.getAreaName());
            area.setAreaSketch(up.getUrl()); // <- CLAVE: nunca null
            area.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

            EntityArea saved = objRepoA.save(area);
            return convertToDTOA(saved);

        } catch (IOException io) {
            throw new RuntimeException("Error de I/O al subir el mapa del área.", io);
        } catch (RuntimeException ex) {
            throw ex;
        }
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

    //Método que permite registar locaciones en un área en específico (Usada en el POST masivo)
    private EntityLocation toEntityLocation(String idBusiness, String idArea, DTOLocationCreate dto) {
        EntityLocation loc = new EntityLocation();
        loc.setLocationName(dto.getLocationName());
        loc.setIdArea(em.getReference(EntityArea.class, idArea));
        loc.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
        return loc;
    }

    private List<DTOEmployee> assignEmployeesToArea(String idBusiness, String idArea, List<String> employeeIds) {
        //Llamamos las entidades de empresa y área con las cargas perezosas, haciendo referencia a la clase con su ID
        EntityArea areaRef = em.getReference(EntityArea.class, idArea);
        EntityBusinessInfo bizRef = em.getReference(EntityBusinessInfo.class, idBusiness);

        if (employeeIds != null) {
            for (String empId : employeeIds) {
                if (empId == null || empId.isBlank()) continue;

                // Evita duplicado (mismo área + mismo empleado + misma empresa)
                boolean exists = objRepoAE.existsByIdArea_IdAreaAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(
                        idArea, empId, idBusiness);
                if (!exists) {
                    EntityAreaEmployee link = new EntityAreaEmployee();
                    link.setIdArea(areaRef);
                    link.setIdEmployee(em.getReference(EntityEmployee.class, empId));
                    link.setIdBusiness(bizRef);
                    objRepoAE.save(link);
                }
            }
        }

        //Lista completa de empleados asignados al área (útil para el front)
        List<EntityAreaEmployee> links = objRepoAE.findByIdArea_IdAreaAndIdBusiness_IdBusiness(idArea, idBusiness);
        List<DTOEmployee> out = new ArrayList<>();
        for (EntityAreaEmployee link : links) {
            String empId = link.getIdEmployee().getIdEmployee();
            out.add(objServiceE.getEmployeeById(empId, idBusiness));
        }
        return out;
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