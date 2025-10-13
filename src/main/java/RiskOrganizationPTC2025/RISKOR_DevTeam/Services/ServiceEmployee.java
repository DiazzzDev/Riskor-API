package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryRoles;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryUser;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec.EmployeeSpecs;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilPasswordGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

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

    @Autowired
    private RepositoryRoles objRepoR;

    private static final String defaultURL = "https://res.cloudinary.com/dmv1q774l/image/upload/v1758774597/DefaultPic_c3wznx.png";

    //region GETs (Activos, inactivos, por ID, todos)
    @Transactional(readOnly = true)
    //Uso de transactional con readonly true en este método para que la db aplique optimizaciones de lectura
    public DTOEmployee getEmployeeById(String idEmployee, String idBusiness) {
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idEmployee));
        return convertToDTOE(employee);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getInactiveEmployees(String idBusiness, int page, int size, String employeeInfo) {
        //Ordenamos el resultado por los campos nombre y apellido de manera ASCENDENTE
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.inactiveInBusiness(idBusiness), //status = 'F'
                EmployeeSpecs.searchQ(employeeInfo)           //nombre/dui/email (opcional)
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);
        return result.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getActiveEmployees(String idBusiness, int page, int size, String employeeInfo) {
        //Ordenamos el resultado por los campos nombre y apellido de manera ASCENDENTE
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.activeInBusiness(idBusiness), //status = 'T'
                EmployeeSpecs.searchQ(employeeInfo)         //nombre/dui/email (opcional)
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);
        return result.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getAllEmployees(String idBusiness, int page, int size, String employeeInfo) {
        //Ordenamos el resultado por los campos nombre y apellido de manera ASCENDENTE
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.byBusiness(idBusiness),  // sin filtro de status
                EmployeeSpecs.searchQ(employeeInfo)    // nombre/dui/email (opcional)
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);

        return result.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getEmployeesNotInTraining(
            String idBusiness, String idTraining, int page, int size,
            String employeeInfo, String role, String idEmployeePosition
        ){
        //Ordenamos el resultado por los campos nombre y apellido de manera ASCENDENTE
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.activeInBusiness(idBusiness),         //Activos en empresa (status = 'T')
                EmployeeSpecs.notInTraining(idTraining),            //NO pertenecen a la capacitación
                EmployeeSpecs.searchQ(employeeInfo),                //Nombre/dui/email (opcional)
                EmployeeSpecs.byRole(role),                         //Rol (opcional)
                EmployeeSpecs.byPosition(idEmployeePosition)        //Cargo (opcional)
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);
        return result.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getTrainingEmployees(String idBusiness, String idTraining, int page, int size, String employeeInfo) {
        //Ordenamos el resultado por los campos nombre y apellido de manera ASCENDENTE
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.activeInBusiness(idBusiness),         //activos en la empresa
                EmployeeSpecs.inTraining(idTraining),               //Capacitación
                EmployeeSpecs.searchQ(employeeInfo)                 //nombre/dui/email (opcional)
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);
        return result.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getWithoutCommittee(
            String idBusiness, int page, int size,
            String employeeInfo, String role, String idEmployeePosition
    ) {
        //Permitir mostrar de manera ascendente los resultados
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.base(idBusiness),
                EmployeeSpecs.searchQ(employeeInfo),          //opcional
                EmployeeSpecs.byRole(role),                   //opcional
                EmployeeSpecs.byPosition(idEmployeePosition)  //opcional
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);
        return result.map(this::convertToDTOE);
    }

    //Obtener los datos de un empleado
    @Transactional(readOnly = true)
    public DTOEmployee getCommitteeById(String idEmployee, String idBusiness) {
        if (idEmployee.isBlank()) throw new IllegalArgumentException("El id del empleado es necesario");

        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idEmployee));
        return convertToDTOE(employee);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getCommitteeEmployees(
            String idBusiness, int page, int size,
            String employeeInfo, String role, String idEmployeePosition
        ){

        //Permitir mostrar de manera ascendente los resultados
        Sort sort = Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("firstName")
        );

        //Ahora la página que va a devolver estará ordenada y con el tamaño que se decida en la solicitud
        Pageable pageable = PageRequest.of(page, size, sort);

        //Realizamos la búsqueda con todo lo solicitado
        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.inCommittee(idBusiness),        // <- base “en comité”
                EmployeeSpecs.searchQ(employeeInfo),          // búsqueda por nombre/dui/email
                EmployeeSpecs.byRole(role),                   // filtro por rol (EntityRoles.roleName)
                EmployeeSpecs.byPosition(idEmployeePosition)  // filtro por cargo (ID o nombre)
        );

        Page<EntityEmployee> result = objRepoE.findAll(spec, pageable);
        return result.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getEmployeesInArea(
            String idBusiness, String idArea, String q, int page, int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending().and(Sort.by("firstName").ascending()));

        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.activeInBusiness(idBusiness),
                EmployeeSpecs.inArea(idBusiness, idArea),
                EmployeeSpecs.searchQ(q)
        );

        Page<EntityEmployee> data = objRepoE.findAll(spec, pageable);
        return data.map(this::convertToDTOE);
    }

    @Transactional(readOnly = true)
    public Page<DTOEmployee> getEmployeesNotInArea(
            String idBusiness, String idArea, String q,
            int page, int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending().and(Sort.by("firstName").ascending()));

        Specification<EntityEmployee> spec = Specification.allOf(
                EmployeeSpecs.activeInBusiness(idBusiness),   // solo activos de la empresa
                EmployeeSpecs.notInArea(idBusiness, idArea),  // NO pertenecen a ese idArea
                EmployeeSpecs.searchQ(q)                      // nombre/dui/correo (opcional)
        );

        Page<EntityEmployee> data = objRepoE.findAll(spec, pageable);
        return data.map(this::convertToDTOE);
    }
    //endregion

    //POST Principal al crear un empleado
    //Haremos uso de transactional con rollback en caso de que un error suceda y no quede un USUARIO FLOTANTE
    //POST Principal al crear un empleado
    @Transactional(rollbackFor = Exception.class)
    public DTOEmployee postEmployee(@Valid DTOEmployee dtoE, String idBusiness, MultipartFile image, boolean isRegister) {
        if (dtoE == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Verificaciones de unicidad
        if (objRepoU.existsById(dtoE.getUsername())) {
            throw new IllegalStateException("El username ya existe (global).");
        }
        if (objRepoE.existsByDui(dtoE.getDui())) {
            throw new IllegalStateException("El DUI ya existe.");
        }
        if (objRepoE.existsByUsername_UsernameAndIdBusiness_IdBusiness(dtoE.getUsername(), idBusiness.toUpperCase())) {
            throw new IllegalStateException("Ya existe un empleado con ese usuario en esta empresa");
        }

        DTOCloudinary up = null;
        String secureRandomPassword = null;
        EntityEmployee createdEmployee = null; // Variable para usar después del rollback
        try {
            //Crear el usuario
            EntityUser user = new EntityUser();
            user.setUsername(dtoE.getUsername());

            //Genera y guarda la contraseña segura
            secureRandomPassword = passwordGenerator.generateSecureRandomString();
            user.setPassword(argon2id.encode(secureRandomPassword));
            user.setStatus("T");
            LocalDate createdAt = LocalDate.now();
            user.setCreationDate(createdAt);

            EntityUser managedUser = objRepoU.save(user);

            //Subir la imagen a Cloudinary
            if (image != null && !image.isEmpty()) {
                up = cloudinary.uploadImage(image, "RISKOR/Person-Photo/");
                dtoE.setPhoto(up.getUrl());
            } else {
                dtoE.setPhoto(defaultURL);
            }

            //Guardar la información del empleado
            EntityEmployee employee = convertToEntityE(dtoE, idBusiness.toUpperCase(), isRegister);
            employee.setUsername(managedUser); // Asignamos el EntityUser gestionado

            createdEmployee = objRepoE.save(employee);

            //Devolver el DTO antes de la llamada de correo para que el Controller retorne 201
            DTOEmployee resultDTO = convertToDTOE(createdEmployee);

            //Lógica de envío de correo
            //Se ejecuta aquí, pero si falla, no revierte la creación del empleado.
            try {
                // Usamos los datos de la entidad persistida (createdEmployee y managedUser)
                serviceEmailSender.sendWelcomeTemplate(
                        createdEmployee.getEmployeeEmail(),
                        "¡Tu usuario en RISKOR ha sido creado!",
                        "RISKOR",
                        createdEmployee.getFirstName(),
                        managedUser.getUsername(),
                        secureRandomPassword,
                        createdEmployee.getIdBusiness().getNameBusiness(),
                        managedUser.getCreationDate().toString() // Conversión de LocalDate a String
                );
            } catch (Exception emailEx) {
                //No hacemos throw para no causar el rollback de la base de datos.
                System.err.println("ADVERTENCIA: El empleado " + createdEmployee.getIdEmployee() + " fue creado, pero el correo falló. Detalle: " + emailEx.getMessage());
            }
            return resultDTO;
        } catch (IOException e) {
            // Si hay error en Cloudinary/IOException, limpiamos la imagen subida.
            handleCloudinaryCleanup(up);
            throw new RuntimeException("Error de I/O (Cloudinary) al registrar el empleado.", e);
        } catch (RuntimeException ex) {
            handleCloudinaryCleanup(up);
            throw ex;
        }
    }

    private void handleCloudinaryCleanup(DTOCloudinary up) {
        if (up != null && up.getPublicId() != null) {
            try {
                cloudinary.deleteByPublicId(up.getPublicId());
            } catch (Exception ignore) {
                // Ignoramos el error de limpieza para no ocultar la excepción original.
            }
        }
    }

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
        int years = Period.between(dtoE.getBirthdate(), LocalDate.now()).getYears();
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

        DTOCloudinary up = null;
        try {
            if (image != null && !image.isEmpty()) {
                //Subir nueva foto
                up = cloudinary.uploadImage(image, "RISKOR/Person-Photo/");
                String oldUrl = employee.getPhoto();
                employee.setPhoto(up.getUrl()); // apuntar a la nueva

                //Borrar la anterior SOLO si no era la default
                if (oldUrl != null && !isDefaultPhoto(oldUrl)) {
                    String oldPid = extractPublicIdFromUrl(oldUrl);
                    // equalsIgnoreCase es null-safe para el argumento
                    if (oldPid != null && !oldPid.equalsIgnoreCase(up.getPublicId())) {
                        try { cloudinary.deleteByPublicId(oldPid); } catch (Exception ignore) {}
                    }
                }
            }
            // No hace falta save(); @Transactional hará flush
            return convertToDTOE(employee);
        } catch (Exception ex) {
            // Si ya subimos imagen nueva y la transacción falla luego, limpia en Cloudinary
            if (up != null && up.getPublicId() != null) {
                try { cloudinary.deleteByPublicId(up.getPublicId()); } catch (Exception ignore) {}
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

    //PUT para agregar un empleado a un comité con su posición respectiva
    @Transactional(rollbackFor = Exception.class)
    public DTOEmployee putEmployeeCommittee(@Valid DTOEmployee dto, String idBusiness, String idEmployee) {
        if (dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para esta empresa"));

        employee.setIdCommitteePosition(em.getReference(EntityComittePosition.class, dto.getIdCommitteePosition()));
        employee.setIdCommitteeRole(em.getReference(EntityComitteRole.class, dto.getIdCommitteeRole()));

        return convertToDTOE(employee); //@Transactional se encarga de actualizar en los setters
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean putUserPassword(String email, String newPassword) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("El email es requerido");
        if (newPassword == null || newPassword.isBlank()) throw new IllegalArgumentException("La nueva contraseña es requerida");

        //Buscar empleado por correo (asegúrate de que el repository tenga este método)
        EntityEmployee employee = objRepoE.findByEmployeeEmailIgnoreCase(email).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con el correo: " + email));

        //Obtener el username asociado
        EntityUser linkedUser = employee.getUsername();
        if (linkedUser == null || linkedUser.getUsername() == null || linkedUser.getUsername().isBlank()) {
            throw new EntityNotFoundException("No existe usuario asociado al empleado con correo: " + email);
        }

        //Obtener el usuario gestionado desde el repo (opcional: em.getReference también serviría)
        String username = linkedUser.getUsername();
        EntityUser managedUser = objRepoU.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));

        //Setear contraseña nueva (encriptada) y persistir
        managedUser.setPassword(argon2id.encode(newPassword));
        objRepoU.save(managedUser); // save() es seguro aunque la entidad esté en contexto transaccional

        return true;
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
        dtoE.setEmployeePosition(employee.getIdEmployeePosition() != null ? employee.getIdEmployeePosition().getEmployeePosition() : null);

        dtoE.setIdBusiness(employee.getIdBusiness() != null ? employee.getIdBusiness().getIdBusiness() : null);
        return dtoE;
    }

    private EntityEmployee convertToEntityE(DTOEmployee dtoEmployee, String idBusiness, boolean isRegister) {
        EntityEmployee employee = new EntityEmployee();

        //Asignación directa de campos
        employee.setIdEmployee(dtoEmployee.getIdEmployee());
        employee.setFirstName(dtoEmployee.getFirstName());
        employee.setLastName(dtoEmployee.getLastName());
        employee.setGender(dtoEmployee.getGender());
        employee.setBirthdate(dtoEmployee.getBirthdate());

        //Calcula la edad del empleado registrado
        int years = Period.between(dtoEmployee.getBirthdate(), LocalDate.now()).getYears();
        if (years < 18) throw new IllegalArgumentException("Fecha de nacimiento inválida, debe ser mayor de edad");
        employee.setAge(years);

        employee.setDui(dtoEmployee.getDui());
        employee.setAffiliationISSS(dtoEmployee.getAffiliationISSS());
        employee.setAddress(dtoEmployee.getAddress());
        employee.setPersonalPhone(dtoEmployee.getPersonalPhone());

        employee.setPhoto(dtoEmployee.getPhoto()); //Agregar foto que se colocó en el dto

        //En caso no se envíe foto se guarda la default
        if (isBlank(employee.getPhoto())) {
            employee.setPhoto(defaultURL);
        }

        employee.setEmployeeEmail(dtoEmployee.getEmployeeMail());
        employee.setStartDate(dtoEmployee.getStartDate());
        employee.setEndDate(dtoEmployee.getEndDate());

        //Conversión de campos FKs
        //Se usa getReference para evitar cargar la entidad completa (Carga perezosa)
        if (dtoEmployee.getUsername() != null) {
            employee.setUsername(em.getReference(EntityUser.class, dtoEmployee.getUsername()));
        }

        if (dtoEmployee.getIdRole() != null) {
            if(isRegister){
                String roleId = objRepoR.findAdministratorId().orElseThrow(() -> new EntityNotFoundException("El rol 'ADMINISTRADOR' no fue encontrado en la base de datos."));
                employee.setIdRole(em.getReference(EntityRoles.class, roleId));
            }else{
                employee.setIdRole(em.getReference(EntityRoles.class, dtoEmployee.getIdRole()));
            }
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
        DTOCloudinary up = cloudinary.uploadImage(image, folder);

        //Actualizar la URL en el área
        String oldUrl = employee.getPhoto();
        employee.setPhoto(up.getUrl());

        if (oldUrl != null && !isDefaultPhoto(oldUrl)) {
            String oldPid = extractPublicIdFromUrl(oldUrl);
            if (oldPid != null && !oldPid.equalsIgnoreCase(up.getPublicId())) {
                try { cloudinary.deleteByPublicId(oldPid); } catch (Exception ignore) {}
            }
        }

        return convertToDTOE(employee); //Devolvemos todo en formato JSON
    }

    //Eliminar - Se va a eliminar solamente cuando se ingrese ELIMINAR FOTO, porque el empleado no se borra
    public DTOEmployee deletePhoto(String idEmployee, String idBusiness) throws IOException {
        EntityEmployee employee = objRepoE.findByIdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        String currentUrl = employee.getPhoto(); //Guardamos el url de la foto actual

        // Si ya es la default o está vacío, solo asegura la default y sal
        if (isDefaultPhoto(currentUrl) || isBlank(currentUrl)) {
            employee.setPhoto(defaultURL);
            return convertToDTOE(employee);
        }

        //Intento por ubicación del archivo
        String expectedPublicIdWithFolder = "RISKOR/Person-Photo/" + idEmployee.toUpperCase();
        try { cloudinary.deleteByPublicId(expectedPublicIdWithFolder); } catch (Exception ignore) {}

        //Intento por public_id real desde la URL
        String fromUrl = extractPublicIdFromUrl(currentUrl);
        if (fromUrl != null && !fromUrl.equalsIgnoreCase(expectedPublicIdWithFolder)) {
            try { cloudinary.deleteByPublicId(fromUrl); } catch (Exception ignore) {}
        }

        employee.setPhoto(defaultURL); //Limpiar campo en DB y agregar la foto por defecto
        return convertToDTOE(employee);
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
    private boolean isDefaultPhoto(String url) {
        return url != null && url.equalsIgnoreCase(defaultURL);
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