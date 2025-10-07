package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccident;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accidents")
@Validated
public class ControllerAccident {
    @Autowired
    private ServiceAccident objServiceA;

    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getAccident/{idAccident}")
    public ResponseEntity<?> getAccidentById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idAccident
    ) {
        try {
            return ResponseEntity.ok(objServiceA.getById(idAccident, idBusiness));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Accidente no encontrado",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el accidente",
                    "detail", e.getMessage()
            ));
        }
    }

    //MAIN GET del apartado - No hay request obligatorios pero al mandarlos especifica la precisión del resultado
    //Útil en accidentes reportados y verificamos
    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getAccidents")
    public ResponseEntity<?> getAccidents(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String statusId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, name = "employeeInfo") String employeeInfo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            if (size < 0 || size > 30) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "El tamaño de la página debe estar entre 5 y 30"
                ));
            }
            if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "toDate no puede ser menor que fromDate"
                ));
            }

            return ResponseEntity.ok(objServiceA.search(idBusiness, employeeId, statusId, fromDate, toDate, employeeInfo, page, size));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar accidentes",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postAccident")
    public ResponseEntity<?> postAccident(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestAttribute("auth.email") String emailEmployee,
            @Valid @RequestBody DTOAccident dtoA
    ) {
        try {
            dtoA.setIdBusiness(idBusiness); // fijamos empresa desde la ruta
            dtoA.setSentBy(emailEmployee);  // Establecer el email del empleado en el DTO

            DTOAccident answer = objServiceA.postAccident(dtoA, idBusiness, emailEmployee);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Accidente reportado correctamente, Success",
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
                    "message", "Error al registrar el accidente",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/putAccident/{idAccident}")
    public ResponseEntity<?> putAccident(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOAccident dto,
            BindingResult dataResult,
            @PathVariable String idAccident
    ) {
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Errores de validación",
                    "errors", errors
            ));
        }
        try {
            dto.setIdBusiness(idBusiness);
            DTOAccident answer = objServiceA.putAccident(dto, idAccident, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Accidente modificado correctamente, Success",
                    "data", answer
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Accidente no encontrado"
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
                    "message", "Error al actualizar el accidente",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/deleteAccident/{idAccident}")
    public ResponseEntity<?> deleteAccident(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idAccident
    ) {
        try {
            boolean ok = objServiceA.removeAccident(idAccident, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del accidente no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Accidente eliminado correctamente, Success"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Accidente no encontrado"
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "No se puede eliminar: registro referenciado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el accidente",
                    "detail", e.getMessage()
            ));
        }
    }
}