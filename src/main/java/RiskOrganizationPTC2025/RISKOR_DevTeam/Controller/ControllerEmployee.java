package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataDuplicate;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEmployee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@Validated
public class ControllerEmployee {
    @Autowired
    private ServiceEmployee objServiceE;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    //Método para buscar un empleado por DUI
    @GetMapping("/getEmployee/{dui}")
    public ResponseEntity<DTOEmployee> getEmployeeByDui(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String dui
        ){
        return ResponseEntity.ok(objServiceE.getEmployeeByDui(dui, idBusiness));
    }

    //Método para obtener todos los empleados de la empresa que no pertenecen al comité
    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("getEmployeesWithoutCommittee")
    public ResponseEntity<Page<DTOEmployee>> getWithoutCommittee(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String employeeInfo,      //Nombre/dui/email
            @RequestParam(required = false) String role,              //Administrador / Mantenimiento / Empleado
            @RequestParam(required = false, name = "idEmployeePosition") String idEmployeePosition  //Contador / Ingeniero / Etc...
    ){
        if(size <= 0 || size > 50){
            return ResponseEntity.badRequest().body(Page.empty());
        }
        Page<DTOEmployee> committee = objServiceE.getWithoutCommittee(idBusiness, page, size, employeeInfo, role, idEmployeePosition);
        return ResponseEntity.ok(committee);
    }

    //Obtener todos los empleados que pertenecen al comité de la empresa
    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getCommitteeEmployees")
    public ResponseEntity<Page<DTOEmployee>> getCommittee(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String employeeInfo,        //Nombre/dui/email
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String idEmployeePosition
    ){
        if(size <= 0 || size > 50){
            return ResponseEntity.badRequest().build();
        }
        Page<DTOEmployee> committee = objServiceE.getCommitteeEmployees(idBusiness, page, size, employeeInfo, role, idEmployeePosition);
        return ResponseEntity.ok(committee);
    }

    //Método para obtener un empleado de comité específico
    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getCommitteeById/{idEmployee}")
    public ResponseEntity<DTOEmployee> getEmployeeCommitteeById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
    ){
        DTOEmployee employee = objServiceE.getCommitteeById(idEmployee, idBusiness);
        return ResponseEntity.ok(employee);
    }

    //Obtener toda la información de un empleado
    @GetMapping("/{idEmployee}/profile")
    public ResponseEntity<?> getEmployeeById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
    ){
        DTOEmployee employees = objServiceE.getEmployeeById(idEmployee, idBusiness);
        return ResponseEntity.ok(employees);
    }

    //Obtenemos todos los empleados inactivos
    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getEmployees/inactiveEmployees")
    public ResponseEntity<Page<DTOEmployee>> getInactiveEmployees(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "employeeInfo") String employeeInfo  //Nombre/dui/email
    ){
        if(size <= 0 || size > 50){
            return ResponseEntity.badRequest().build();
        }
        Page<DTOEmployee> employees = objServiceE.getInactiveEmployees(idBusiness, page, size, employeeInfo);
        return ResponseEntity.ok(employees);
    }

    //Método para obtener todos los empleados ACTIVOS de la empresa - GET PRINCIPAL
    @GetMapping("/getEmployees/activeEmployees")
    public ResponseEntity<Page<DTOEmployee>> getActiveEmployees(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "employeeInfo") String employeeInfo  //Nombre/dui/email
    ){
        if(size <= 0 || size > 50){
            return ResponseEntity.badRequest().build();
        }
        Page<DTOEmployee> employees = objServiceE.getActiveEmployees(idBusiness, page, size, employeeInfo);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/getEmployees")
    public ResponseEntity<Page<DTOEmployee>> getEmployees(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "employeeInfo") String employeeInfo
    ){
        if(size <= 0 || size > 50){
            return ResponseEntity.badRequest().build();
        }
        Page<DTOEmployee> employees = objServiceE.getAllEmployees(idBusiness, page, size, employeeInfo);
        return ResponseEntity.ok(employees);
    }

    //Método para buscar empleados que NO están dentro de una capacitación
    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getEmployees/notInTraining/{idTraining}")
    public ResponseEntity<?> getEmployeesNotInTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTraining,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "employeeInfo") String employeeInfo,
            @RequestParam(required = false, name = "role") String role,
            @RequestParam(required = false, name = "idEmployeePosition") String idEmployeePosition
    ) {
        if (page < 0 || size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Parámetros inválidos",
                    "message", "page >= 0, 1 <= size <= 50"
            ));
        }
        return ResponseEntity.ok(objServiceE.getEmployeesNotInTraining(idBusiness, idTraining, page, size, employeeInfo, role, idEmployeePosition));
    }

    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getEmployees/{idTraining}")
    public ResponseEntity<?> getTrainingEmployees(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTraining,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "employeeInfo") String employeeInfo
    ) {
        if (page < 0 || size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status","Parámetros inválidos","message","page >= 0, 1 <= size <= 50"
            ));
        }
        return ResponseEntity.ok(objServiceE.getTrainingEmployees(idBusiness, idTraining, page, size, employeeInfo));
    }

    //Método para crear el empleado desde el formulario de EMPLEADOS - Frontend
    @PreAuthorize("hasRole('Administrador')")
    @PostMapping(value = "/postEmployee",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postDataEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestPart("dto") String dtoE,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ){
        try {
            DTOEmployee dto = mapper.readValue(dtoE, DTOEmployee.class);
            dto.setIdBusiness(idBusiness);
            DTOEmployee answer = objServiceE.postEmployee(dto, idBusiness, photo);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Empleado creado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    //Método para actualizar la información principal del empleado - No podrá cambiar detalles de usuario aquí
    @PreAuthorize("hasRole('Administrador')")
    @PutMapping(value = "/putEmployee/{idEmployee}",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee,
            @RequestPart("dto") String dtoE,
            BindingResult dataResult,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            DTOEmployee dto = mapper.readValue(dtoE, DTOEmployee.class);
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOEmployee answer = objServiceE.putEmployee(dto, idEmployee, idBusiness, photo);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Empleado modificado correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (ExceptionDataDuplicate e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "Error", "Datos duplicados, intentelo denuevo",
                    "Campo duplicado", e.getDuplicateData()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al modificar el empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    //Put para añadir o actualizar committe
    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/putCommitteeEmployees/{idEmployee}")
    public ResponseEntity<?> putCommittee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee,
            @RequestBody DTOEmployee dto,
            BindingResult dataResult
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            DTOEmployee answer = objServiceE.putEmployeeCommittee(dto, idBusiness, idEmployee);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al añadir el empleado al comitte",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Empleado agregado al committe correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (ExceptionDataDuplicate e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "Error", "Datos duplicados, intentelo denuevo",
                    "Campo duplicado", e.getDuplicateData()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al añadir al empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    // Quitar al empleado del comité (solo limpia idComittePosition e idComitteRole)
    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/removeCommitteeEmployee/{idEmployee}")
    public ResponseEntity<?> removeFromCommittee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
    ) {
        try {
            boolean removed = objServiceE.removeEmployeeFromCommittee(idEmployee, idBusiness);
            if (!removed) {
                // Ya estaba fuera del comité
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(Map.of(
                        "status", "Sin cambios",
                        "message", "El empleado ya no pertenece al comité"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Empleado removido del comité correctamente, Success",
                    "removed", true
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El empleado no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al remover al empleado del comité",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/{idEmployee}/photo")
    public ResponseEntity<?> replacePhoto(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTOEmployee updated = objServiceE.updatePhoto(idEmployee, idBusiness, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Foto actualizada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El empleado no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al subir la foto del empleado",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la foto del empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    //Delete de la fotografía del empleado
    @DeleteMapping("/{idEmployee}/photo")
    public ResponseEntity<?> deletePhoto(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
    ) {
        try {
            DTOEmployee updated = objServiceE.deletePhoto(idEmployee, idBusiness);
            return ResponseEntity.ok(Map.of(
                    "status", "Fotografía eliminada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La fotografía no fue encontrada o no existe",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la fotografía del empleado",
                    "detail", e.getMessage()
            ));
        }
    }
}