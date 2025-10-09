package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEmployeePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEmployeePosition;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employeePosition")
@Validated
public class ControllerEmployeePosition {
    @Autowired
    private ServiceEmployeePosition objServiceEP;

    @GetMapping("/{idEmployeePosition}")
    public ResponseEntity<?> getEmployeesPositionsById(
            @PathVariable String idEmployeePosition,
            @RequestAttribute("auth.business") String idBusiness
    ){
        try {
            if (idEmployeePosition == null || idEmployeePosition.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "error", "idEmployeePosition es requerido"
                ));
            }
            return ResponseEntity.ok(objServiceEP.getPositionById(idBusiness, idEmployeePosition));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Cargo no encontrado",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el cargo laboral",
                    "detail", e.getMessage()
            ));
        }
    }

    @GetMapping("/getEmployeesPositions")
    public ResponseEntity<Page<DTOEmployeePosition>> getEmployeesPositions(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ){
        if(size <= 0 || size > 30){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(objServiceEP.getEmployeePosition(idBusiness, page, size));
    }

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping("/postEmployeePosition") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postEmployeePosition(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEmployeePosition dto
    ) {
        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOEmployeePosition answer = objServiceEP.postEmployeePosition(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Cargo laboral registrado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el cargo laboral",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/putEmployeePosition/{idEmployeePosition}")
    public ResponseEntity<?> putEmployeePosition(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEmployeePosition dto,
            @PathVariable String idEmployeePosition,
            BindingResult dataResult) {

        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOEmployeePosition answer = objServiceEP.putEmployeePosition(dto, idEmployeePosition, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Cargo laboral modificado correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el cargo laboral",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/deleteEmployeePosition/{idEmployeePosition}")
    public ResponseEntity<?> deleteEmployeePosition(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployeePosition
    ) {
        try {
            boolean ok = objServiceEP.removeEmployeePosition(idEmployeePosition, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del cargo laboral no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del cargo laboral no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Cargo laboral eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el cargo laboral",
                    "detail", e.getMessage()
            ));
        }
    }
}
