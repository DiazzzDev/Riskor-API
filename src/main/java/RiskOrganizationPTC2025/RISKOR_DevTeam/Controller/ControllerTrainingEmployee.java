package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTrainingEmployee;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainingEmployee")
@Validated
public class ControllerTrainingEmployee {
    @Autowired
    private ServiceTrainingEmployee objServiceTE;

    @GetMapping("/getTrainingEmployee/getIdTrainingEmployee/{idTrainingEmployee}")
    public ResponseEntity<?> getTrainingEmployeeById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTrainingEmployee
        ){
        try {
            DTOTrainingEmployee trainingEmployee = objServiceTE.getTrainingEmployeeById(idTrainingEmployee, idBusiness);
            return ResponseEntity.ok(trainingEmployee);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Registro no encontrado",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el registro",
                    "detail", e.getMessage()
            ));
        }
    }

    @GetMapping("/getTrainingEmployee/employee/{idEmployee}")
    public ResponseEntity<?> getTrainingEmployeesByTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
        ){
        try {
            List<DTOTrainingEmployee> list = objServiceTE.getTrainingEmployeeByEmployee(idEmployee, idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar registros por empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping("/postTrainingEmployee")
    public ResponseEntity<?> postTrainingEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTrainingEmployee dto
        ){
        try {
            dto.setIdBusiness(idBusiness); //Fijamos empresa desde la ruta, no desde el JSON
            DTOTrainingEmployee answer = objServiceTE.postTrainingEmployee(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Empleado agregado a la capacitación correctamente, Success",
                    "data", answer
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "Violación de integridad de datos (FK/UK)"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el empleado dentro de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/putTrainingEmployee/{idTrainingEmployee}")
    public ResponseEntity<?> putTrainingEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTrainingEmployee dto,
            BindingResult dataResult,
            @PathVariable String idTrainingEmployee
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if(dataResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Errores de validación",
                    "errors", errors
            ));
        }
        try {
            dto.setIdBusiness(idBusiness);
            DTOTrainingEmployee answer = objServiceTE.putTrainingEmployee(dto, idTrainingEmployee, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Empleado dentro de la capacitación modificado correctamente, Success",
                    "data", answer
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Registro no encontrado"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "Violación de integridad de datos (FK/UK)"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar al empleado dentro de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("{idTraining}/put/{idEmployee}")
    public ResponseEntity<?> takeAttendance(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestBody DTOTrainingEmployee dto,
            BindingResult dataResult,
            @PathVariable String idTraining,
            @PathVariable String idEmployee
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if(dataResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Errores de validación",
                    "errors", errors
            ));
        }
        try {
            dto.setIdBusiness(idBusiness);
            DTOTrainingEmployee answer = objServiceTE.takeAttendance(dto, idTraining, idEmployee, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Empleado dentro de la capacitación modificado correctamente, Success",
                    "data", answer
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Registro no encontrado"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "Violación de integridad de datos (FK/UK)"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar al empleado dentro de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("{idTraining}/delete/{idEmployee}")
    public ResponseEntity<?> deleteTrainingEmployeeFromTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTraining,
            @PathVariable String idEmployee
    ){
        try {
            boolean ok = objServiceTE.removeEmployeeFromTraining(idTraining, idEmployee, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                                "Error, ID no encontrado", "ID del empleado en capacitación no encontrado")
                        .body(Map.of(
                                "status", "No encontrado, Error",
                                "message", "El ID del empleado asignado a la capacitación no ha sido encontrado",
                                "timeStamp", Instant.now().toString()
                        ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Empleado eliminado de la capacitación correctamente, Success"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Registro no encontrado"
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "No se puede eliminar: registro referenciado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar al empleado de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/deleteTrainingEmployee/{idTrainingEmployee}")
    public ResponseEntity<?> deleteTrainingEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTrainingEmployee
    ){
        try {
            boolean ok = objServiceTE.removeTrainingEmployee(idTrainingEmployee, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                                "Error, ID no encontrado", "ID del empleado en capacitación no encontrado")
                        .body(Map.of(
                                "status", "No encontrado, Error",
                                "message", "El ID del empleado asignado a la capacitación no ha sido encontrado",
                                "timeStamp", Instant.now().toString()
                        ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Empleado eliminado de la capacitación correctamente, Success"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Registro no encontrado"
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "No se puede eliminar: registro referenciado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar al empleado de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }
}
