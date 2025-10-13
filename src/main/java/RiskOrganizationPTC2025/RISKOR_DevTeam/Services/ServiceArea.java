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
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public DTOAreaInBusiness getAreaBundle(String idBusiness, String idArea) {
        String biz = idBusiness.toUpperCase();

        // 1) Área
        EntityArea area = objRepoA
                .findByIdAreaAndIdBusiness_IdBusiness(idArea, biz)
                .orElseThrow(() -> new EntityNotFoundException("Área no encontrada para esta empresa"));
        DTOArea dtoArea = convertToDTOA(area);

        // 2) Locaciones del área
        List<EntityLocation> locs = objRepoL.findByIdArea_IdAreaAndIdBusiness_IdBusiness(idArea, biz);
        List<DTOLocation> dtoLocs = new ArrayList<>(locs.size());
        for (EntityLocation l : locs) {
            DTOLocation d = new DTOLocation();
            d.setIdLocation(l.getIdLocation());
            d.setLocationName(l.getLocationName());
            d.setIdArea(idArea);
            d.setIdBusiness(biz);
            dtoLocs.add(d);
        }

        // 3) Empleados asignados al área (reusa tu repo de links)
        List<EntityAreaEmployee> links = objRepoAE.findByIdArea_IdAreaAndIdBusiness_IdBusiness(idArea, biz);
        List<DTOEmployee> dtoEmployees = new ArrayList<>(links.size());
        for (EntityAreaEmployee link : links) {
            String empId = link.getIdEmployee().getIdEmployee();
            dtoEmployees.add(objServiceE.getEmployeeById(empId, biz)); // ya tienes este método
        }

        // 4) Armar respuesta
        DTOAreaInBusiness out = new DTOAreaInBusiness();
        out.setArea(dtoArea);
        out.setLocationsInArea(dtoLocs);
        out.setEmployeesOnArea(dtoEmployees);
        return out;
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

    public DTOAreaInBusiness putAreaBundle(String idBusiness, String idArea, DTOAreaBundleRequest req, MultipartFile image) {
        if (req == null || req.getArea() == null) throw new IllegalArgumentException("Payload inválido: falta objeto area");

        String biz = idBusiness.toUpperCase();

        // 0) Verificar que el área exista y pertenezca a la empresa
        EntityArea area = objRepoA.findByIdAreaAndIdBusiness_IdBusiness(idArea, biz)
                .orElseThrow(() -> new EntityNotFoundException("Área no encontrada para esta empresa"));

        // 1) Actualizar campos básicos del área (nombre)
        DTOArea dtoA = req.getArea();
        if (dtoA.getAreaName() != null && !dtoA.getAreaName().isBlank()) {
            area.setAreaName(dtoA.getAreaName());
        }

        // 2) Si envían imagen, reemplazar el mapa en Cloudinary y actualizar el campo
        if (image != null && !image.isEmpty()) {
            try {
                String folder = "RISKOR/Areas-Sketches/" + biz + "/";
                DTOCloudinary up = cloudinary.uploadImage(image, folder);
                area.setAreaSketch(up.getUrl());
            } catch (IOException io) {
                throw new RuntimeException("Error subiendo nuevo mapa del área.", io);
            }
        }

        // Guardar cambios básicos del área
        EntityArea savedArea = objRepoA.save(area);

        // 3) Crear nuevas locaciones (NOTA: DTOLocationCreate solo trae locationName)
        List<DTOLocation> outLocations = new ArrayList<>();
        if (req.getLocations() != null && !req.getLocations().isEmpty()) {
            for (DTOLocationCreate lc : req.getLocations()) {
                if (lc == null) continue;
                // Solo crear nuevas locaciones con el DTO que tienes ahora
                EntityLocation toSave = toEntityLocation(idBusiness, idArea, lc);
                EntityLocation savedLoc = objRepoL.save(toSave);

                DTOLocation dtoOut = new DTOLocation();
                dtoOut.setIdLocation(savedLoc.getIdLocation());
                dtoOut.setLocationName(savedLoc.getLocationName());
                dtoOut.setIdArea(idArea);
                dtoOut.setIdBusiness(biz);
                outLocations.add(dtoOut);
            }
        }

        // 4) Sincronizar empleados asignados:
        // Obtener enlaces actuales
        List<EntityAreaEmployee> currentLinks = objRepoAE.findByIdArea_IdAreaAndIdBusiness_IdBusiness(idArea, biz);
        Set<String> currentEmployeeIds = currentLinks.stream()
                .map(link -> link.getIdEmployee().getIdEmployee())
                .collect(Collectors.toSet());

        Set<String> requestedEmployeeIds = req.getEmployeeIds() != null ?
                req.getEmployeeIds().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        // Añadir nuevos links
        for (String empId : requestedEmployeeIds) {
            if (!currentEmployeeIds.contains(empId)) {
                EntityAreaEmployee link = new EntityAreaEmployee();
                link.setIdArea(em.getReference(EntityArea.class, idArea));
                link.setIdEmployee(em.getReference(EntityEmployee.class, empId));
                link.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));
                objRepoAE.save(link);
            }
        }

        // Eliminar links que ya no están solicitados (usamos delete(entity) para ser compatibles con tu PK)
        for (EntityAreaEmployee link : currentLinks) {
            String empId = link.getIdEmployee().getIdEmployee();
            if (!requestedEmployeeIds.contains(empId)) {
                objRepoAE.delete(link);
            }
        }

        // 5) Construir la lista final de empleados asignados para devolver
        List<EntityAreaEmployee> finalLinks = objRepoAE.findByIdArea_IdAreaAndIdBusiness_IdBusiness(idArea, biz);
        List<DTOEmployee> employeesOut = new ArrayList<>();
        for (EntityAreaEmployee link : finalLinks) {
            String empId = link.getIdEmployee().getIdEmployee();
            employeesOut.add(objServiceE.getEmployeeById(empId, biz));
        }

        // Agregar las locaciones existentes también al output (si quieres devolver todas las locaciones)
        // Obtenemos todas las locaciones de la DB (incluye las nuevas creadas)
        List<EntityLocation> allLocs = objRepoL.findByIdArea_IdAreaAndIdBusiness_IdBusiness(idArea, biz);
        // Combinar locaciones nuevas (outLocations) con las que vienen de DB evitando duplicados
        Map<String, DTOLocation> locMap = new LinkedHashMap<>();
        for (DTOLocation dl : outLocations) {
            locMap.put(dl.getIdLocation(), dl);
        }
        for (EntityLocation el : allLocs) {
            if (!locMap.containsKey(el.getIdLocation())) {
                DTOLocation d = new DTOLocation();
                d.setIdLocation(el.getIdLocation());
                d.setLocationName(el.getLocationName());
                d.setIdArea(idArea);
                d.setIdBusiness(biz);
                locMap.put(d.getIdLocation(), d);
            }
        }
        List<DTOLocation> locationsOutFinal = new ArrayList<>(locMap.values());

        //Armar DTO de salida
        DTOAreaInBusiness out = new DTOAreaInBusiness();
        out.setArea(convertToDTOA(savedArea));
        out.setLocationsInArea(locationsOutFinal);
        out.setEmployeesOnArea(employeesOut);

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