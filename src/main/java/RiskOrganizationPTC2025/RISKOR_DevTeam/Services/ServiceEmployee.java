package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryUser;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilPasswordGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class ServiceEmployee {
    @Autowired
    private RepositoryEmployee objRepoE;

    @Autowired
    private RepositoryUser objRepoU;

    @Autowired
    private ServiceCloudinary cloudinary;

    @PersistenceContext         //Ayuda a evitar cargar objetos completos en FK con Entity Manager
    private EntityManager em;   //NO sustituye JPA, solo es para hacer referencia a entidades en cargas perezosas

    @Autowired
    private UtilPasswordGenerator passwordGenerator;

    @Autowired
    private ServiceEmailSender serviceEmailSender;

    //Inyección de PasswordEncoder para usar argon2id en encriptación
    @Autowired
    private PasswordEncoder argon2id;

    //region GETs (Activos, inactivos, por ID, todos)
    @Transactional(readOnly = true)
    //Uso de transactional con readonly true en este método para que la db aplique optimizaciones de lectura
    public DTOEmployee getEmployeeById(String idEmployee, String idBusiness) {
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idEmployee));
        return convertToDTOE(employee);
    }

    @Transactional(readOnly = true)
    public DTOEmployee getEmployeeByDui(String dui, String idBusiness) {
        EntityEmployee employee = objRepoE.findByDuiAndIdBusiness_IdBusiness(dui, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado o no pertenece a esta empresa"));
        return convertToDTOE(employee);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getInactiveEmployees(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); //Creación de elemento Pageable para realizar el paginado en el GET

        Page<EntityEmployee> permissionPage = objRepoE.findByIdBusiness_IdBusinessAndUsername_Status(idBusiness.toUpperCase(), "F", pageable); //Importante, los status F son inactivos
        return permissionPage.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getActiveEmployees(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<EntityEmployee> permissionPage = objRepoE.findByIdBusiness_IdBusinessAndUsername_Status(idBusiness.toUpperCase(), "T", pageable); //Importante, los status T son activos
        return permissionPage.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getAllEmployees(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<EntityEmployee> permissionPage = objRepoE.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return permissionPage.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getEmployeesNotInTraining(String idBusiness, String idTraining, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEmployee> data =
                objRepoE.findActiveEmployeesNotInTraining(idBusiness.toUpperCase(), idTraining, pageable);
        return data.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getTrainingEmployees(String idBusiness, String idTraining, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEmployee> data =
                objRepoE.findActiveEmployeesInTraining(idBusiness.toUpperCase(), idTraining, pageable);
        return data.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getWithoutCommittee(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEmployee> data = objRepoE.findByIdBusiness_IdBusinessAndIdCommitteePositionIsNullAndIdCommitteeRoleIsNullAndUsername_Status(idBusiness.toUpperCase(), "T", pageable);
        return data.map(this::convertToDTOE);
    }

    //Obtener los datos de un empleado
    @Transactional(readOnly = true)
    public DTOEmployee getCommitteeById(String idEmployee, String idBusiness) {
        if (idEmployee.isBlank() || idBusiness.isBlank())
            throw new IllegalArgumentException("Los identificadores son necesarios");

        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idEmployee));
        ;
        return convertToDTOE(employee);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getCommitteeEmployees(String idBusiness, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<EntityEmployee> data = objRepoE.findByIdBusiness_IdBusinessAndIdCommitteePositionIsNotNullAndIdCommitteeRoleIsNotNullAndUsername_Status(idBusiness.toUpperCase(), "T", pageable);
        return data.map(this::convertToDTOE);
    }
    //endregion

    //Si algo en el proceso cambió y luego salió mal se revierte lo que si funcionó para evitar problemas (Se hace rollback)
    @Transactional(rollbackFor = Exception.class)
    public DTOEmployee putEmployee(@Valid DTOEmployee dtoE, String idEmployee, String idBusiness, MultipartFile image) throws IOException {
        if (dtoE == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idEmployee));

        employee.setFirstName(dtoE.getFirstName());
        employee.setLastName(dtoE.getLastName());
        employee.setGender(dtoE.getGender());
        employee.setBirthdate(dtoE.getBirthdate());

        //Se vuelve a calcular la edad
        int years = java.time.Period.between(dtoE.getBirthdate(), java.time.LocalDate.now()).getYears();
        if (years < 18) throw new IllegalArgumentException("Fecha de nacimiento inválida, debe ser mayor de edad");
        employee.setAge(years);

        employee.setDui(dtoE.getDui());
        employee.setAffiliationISSS(dtoE.getAffiliationISSS());
        employee.setAddress(dtoE.getAddress());
        employee.setPersonalPhone(dtoE.getPersonalPhone());
        employee.setEmployeeEmail(dtoE.getEmployeeMail());

        //Validación - Consistencia entre fechas
        //Si se modificó la fecha final y la de inicio del empleado y si la final es antes de que inicie va a lanzar una excepción
        if (dtoE.getEndDate() != null && dtoE.getStartDate() != null && dtoE.getEndDate().isBefore(dtoE.getStartDate())) {
            throw new IllegalArgumentException("endDate no puede ser anterior a startDate");
        }

        employee.setStartDate(dtoE.getStartDate());
        employee.setEndDate(dtoE.getEndDate());
        employee.setIdRole(em.getReference(EntityRoles.class, dtoE.getIdRole()));
        employee.setIdEmployeePosition(em.getReference(EntityEmployeePosition.class, dtoE.getIdEmployeePosition()));
        DTOCloudinary up = null;                     // ← para limpieza si algo falla luego
        try {
            if (image != null && !image.isEmpty()) {
                up = cloudinary.uploadImage(image, "RISKOR/Person-Photo/"); // devuelve url + publicId

                String oldUrl = employee.getPhoto();
                employee.setPhoto(up.getUrl()); // apuntar a la nueva

                String oldPid = extractPublicIdFromUrl(oldUrl);
                if (oldPid != null && !oldPid.equalsIgnoreCase(up.getPublicId())) {
                    try {
                        cloudinary.deleteByPublicId(oldPid);
                    } catch (Exception ignore) {
                    }
                }
            }
            // No hace falta save(); @Transactional hará flush
            return convertToDTOE(employee);

        } catch (Exception ex) {
            // Si ya subimos imagen nueva y la transacción falla luego, limpia en Cloudinary
            if (up != null && up.getPublicId() != null) {
                try {
                    cloudinary.deleteByPublicId(up.getPublicId());
                } catch (Exception ignore) {
                }
            }
            throw ex;
        }
    }

    public boolean removeEmployeeFromCommittee(String idEmployee, String idBusiness) {
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));
        employee.setIdCommitteePosition(null);
        employee.setIdCommitteeRole(null);
        return true;
    }

    //POST Principal al crear un empleado
//Haremos uso de transactional con rollback en caso de que un error suceda y no quede un USUARIO FLOTANTE
    //POST Principal al crear un empleado
    @Transactional(rollbackFor = Exception.class)
    public DTOEmployee postEmployee(@Valid DTOEmployee dtoE, String idBusiness, MultipartFile image) {
        // Verificaciones iniciales
        if (dtoE == null) throw new IllegalArgumentException("No pueden haber campos vacíos");
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("La imagen no puede estar vacía");

        // Si el usuario ya existe en esta empresa, lanzamos excepción
        if (objRepoE.existsByUsername_UsernameAndIdBusiness_IdBusiness(dtoE.getUsername(), idBusiness.toUpperCase())) {
            throw new IllegalStateException("Ya existe un empleado con ese usuario en esta empresa");
        }

        DTOCloudinary up = null; // para limpieza si algo falla luego
        String secureRandomPassword = null; // Declarada fuera para usarla en el email

        try {
            // --- 1. Crear y guardar la EntidadUser (¡Se hace antes para obtener la entidad gestionada!) ---
            EntityUser user = new EntityUser();
            user.setUsername(dtoE.getUsername());

            // Genera y guarda la contraseña segura para el email
            secureRandomPassword = passwordGenerator.generateSecureRandomString();
            user.setPassword(argon2id.encode(secureRandomPassword));

            user.setStatus("T");
            user = objRepoU.save(user); // ¡Importante!: Guardamos la entidad y actualizamos 'user' con la entidad gestionada.

            // --- 2. Subir la imagen a Cloudinary ---
            up = cloudinary.uploadImage(image, "RISKOR/Person-Photo/");
            dtoE.setPhoto(up.getUrl()); // Guardar URL en el DTO para el mapeo a entidad

            // --- 3. Guardar la información del empleado ---
            // Convertimos el DTO a la entidad
            EntityEmployee employee = convertToEntityE(dtoE, idBusiness.toUpperCase());

            // ¡LA LÍNEA CRUCIAL FALTANTE!
            // Asignamos el EntityUser que acabamos de guardar al EntityEmployee.
            employee.setUsername(user);

            employee = objRepoE.save(employee);

            // --- 5. Retornar DTO ---
            return convertToDTOE(employee);

        } catch (Exception ex) {
            // Lógica de limpieza idéntica a la de putEmployee:
            // Si ya subimos imagen y la transacción falla luego (ej. error en la DB), limpia en Cloudinary
            if (up != null && up.getPublicId() != null) {
                try {
                    cloudinary.deleteByPublicId(up.getPublicId());
                } catch (Exception ignore) {
                    // Ignoramos el error de limpieza para no ocultar la excepción original.
                }
            }
            // Relanzamos la excepción original para que @Transactional haga el rollback
            return null;
        }
    }

    //PUT para agregar un empleado a un comité con su posición respectiva
    @Transactional(rollbackFor = Exception.class)
    public DTOEmployee putEmployeeCommittee(@Valid DTOEmployee dto, String idBusiness, String idEmployee) {
        if (dto == null) {
            throw new IllegalArgumentException("No pueden haber campos vacíos");
        }

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para esta empresa"));

        employee.setIdCommitteePosition(em.getReference(EntityComittePosition.class, dto.getIdCommitteePosition()));
        employee.setIdCommitteeRole(em.getReference(EntityComitteRole.class, dto.getIdCommitteeRole()));

        //EntityEmployee saved = objRepoE.save(employee); Ya usamos transactional, al cambiarlo JPA se encarga de actualizar el registro
        return convertToDTOE(employee);
    }

    private DTOEmployee convertToDTOE(EntityEmployee employee) {
        DTOEmployee dtoE = new DTOEmployee();
        dtoE.setIdEmployee(employee.getIdEmployee());
        dtoE.setFirstName(employee.getFirstName());
        dtoE.setLastName(employee.getLastName());
        dtoE.setGender(employee.getGender());
        dtoE.setBirthdate(employee.getBirthdate());
        dtoE.setAge(employee.getAge());
        dtoE.setDui(employee.getDui());
        dtoE.setAffiliationISSS(employee.getAffiliationISSS());
        dtoE.setAddress(employee.getAddress());
        dtoE.setPersonalPhone(employee.getPersonalPhone());
        dtoE.setPhoto(employee.getPhoto());
        dtoE.setEmployeeMail(employee.getEmployeeEmail());
        dtoE.setStartDate(employee.getStartDate());
        dtoE.setEndDate(employee.getEndDate());

        //En este campo no se aplica el operador ternario porque no usa cargas perezosas
        dtoE.setUsername(employee.getUsername().getUsername());
        dtoE.setStatus(em.getReference(EntityUser.class, employee.getUsername().getUsername()).getStatus());

        // NOMBRES (null-safe, sin getReference)
        dtoE.setCommittePosition(employee.getIdCommitteePosition() != null ? employee.getIdCommitteePosition().getCommittePositionName() : null);
        dtoE.setCommitteRole(employee.getIdCommitteeRole() != null ? employee.getIdCommitteeRole().getCommitteRoleName() : null);

        //Los siguientes campos son FKs, por lo cual vamos a verificar si tienen un valor con operador ternario
        //Si en la entidad hay un valor en esos campos va a buscar su ID
        //En caso contrario va a mostrar NULL para evitar conflictos, esto por uso de cargas perezosas
        dtoE.setIdRole(employee.getIdRole() != null ? employee.getIdRole().getIdRole() : null);
        //SI Entidad es diferente de nulo mostrará su ID, caso contrario mostrará nulo
        dtoE.setIdCommitteePosition(employee.getIdCommitteePosition() != null ? employee.getIdCommitteePosition().getIdComitteP() : null);
        dtoE.setIdCommitteeRole(employee.getIdCommitteeRole() != null ? employee.getIdCommitteeRole().getIdRole() : null);
        dtoE.setIdEmployeePosition(employee.getIdEmployeePosition() != null ? employee.getIdEmployeePosition().getIdEmployeePosition() : null);
        dtoE.setIdBusiness(employee.getIdBusiness() != null ? employee.getIdBusiness().getIdBusiness() : null);
        return dtoE;
    }

    private EntityEmployee convertToEntityE(DTOEmployee dtoEmployee, String idBusiness) {
        EntityEmployee employee = new EntityEmployee();

        //Asignación directa de campos
        employee.setIdEmployee(dtoEmployee.getIdEmployee());
        employee.setFirstName(dtoEmployee.getFirstName());
        employee.setLastName(dtoEmployee.getLastName());
        employee.setGender(dtoEmployee.getGender());
        employee.setBirthdate(dtoEmployee.getBirthdate());

        //Calcula la edad del empleado registrado
        int years = java.time.Period.between(dtoEmployee.getBirthdate(), java.time.LocalDate.now()).getYears();
        if (years < 18) throw new IllegalArgumentException("Fecha de nacimiento inválida, debe ser mayor de edad");
        employee.setAge(years);
        //employee.setAge(dtoEmployee.getAge());

        employee.setDui(dtoEmployee.getDui());
        employee.setAffiliationISSS(dtoEmployee.getAffiliationISSS());
        employee.setAddress(dtoEmployee.getAddress());
        employee.setPersonalPhone(dtoEmployee.getPersonalPhone());
        employee.setPhoto(dtoEmployee.getPhoto());
        employee.setEmployeeEmail(dtoEmployee.getEmployeeMail());
        employee.setStartDate(dtoEmployee.getStartDate());
        employee.setEndDate(dtoEmployee.getEndDate());

        //Conversión de campos FKs
        //Se usa getReference para evitar cargar la entidad completa (Carga perezosa)
        if (dtoEmployee.getUsername() != null) {
            employee.setUsername(em.getReference(EntityUser.class, dtoEmployee.getUsername()));
        }

        if (dtoEmployee.getIdRole() != null) {
            employee.setIdRole(em.getReference(EntityRoles.class, dtoEmployee.getIdRole()));
        }

        //Por defecto el empleado no va a pertenecer al comité de salud y seguridad ocupacional
        employee.setIdCommitteePosition(null);
        employee.setIdCommitteeRole(null);

        if (dtoEmployee.getIdEmployeePosition() != null) {
            employee.setIdEmployeePosition(em.getReference(EntityEmployeePosition.class, dtoEmployee.getIdEmployeePosition()));
        }
        //No vamos a tomarlo del JSON si el usuario logra saltar de la validación del DTO
        employee.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

        return employee;
    }

    //CRUD Fotografía del empleado
    //Post y PUT
    public DTOEmployee updatePhoto(String idEmployee, String idBusiness, MultipartFile image) throws IOException {
        //Verificar que el área pertenece a la empresa
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Person-Photo/";
        DTOCloudinary secureUrl = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        employee.setPhoto(secureUrl.getUrl());
        return convertToDTOE(employee); //Devolvemos todo en formato JSON
    }

    public String uploadPhoto(MultipartFile image) throws IOException {
        //Subir a la carpeta de cloudinary
        String folder = "RISKOR/Person-Photo/";
        return cloudinary.uploadImage(image, folder).getUrl();
    }

    //Eliminar - Se va a eliminar solamente cuando se ingrese ELIMINAR FOTO, porque el empleado no se borra
    public DTOEmployee deletePhoto(String idEmployee, String idBusiness) throws IOException {
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        String expectedPublicIdWithFolder = "RISKOR/Person-Photo/" + idEmployee.toUpperCase();

        //Se intenta con la convención oficial (idArea como public_id)
        cloudinary.deleteByPublicId(expectedPublicIdWithFolder);

        //Si alguna vez subiste con nombre aleatorio, intenta extraerlo desde la URL
        String url = employee.getPhoto();
        if (url != null) {
            String fromUrl = extractPublicIdFromUrl(url); // ej: RISKOR/Person-Photo/
            if (fromUrl != null && !fromUrl.equalsIgnoreCase(expectedPublicIdWithFolder)) {
                cloudinary.deleteByPublicId(fromUrl);
            }
        }

        employee.setPhoto("Sin fotografía"); //Limpiar campo en DB
        return convertToDTOE(employee);
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
            return after; // p.ej. RISKOR/Person-Photo
        } catch (Exception e) {
            return null;
        }
    }
}