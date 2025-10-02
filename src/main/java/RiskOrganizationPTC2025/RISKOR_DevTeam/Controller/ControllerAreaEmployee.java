package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAreaEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAreaEmployee;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/areaEmployee")
@Validated
@PreAuthorize("hasRole('Administrador')")
public class ControllerAreaEmployee {
    @Autowired
    private ServiceAreaEmployee objServiceAE;

    @GetMapping("/getAreaEmployee")
    public List<DTOAreaEmployee> getAreaEmployee(@RequestAttribute("auth.business") String idBusiness){
        return objServiceAE.getAreaEmployees(idBusiness);
    }

    @PostMapping("/postAreaEmployee")
    public ResponseEntity<?> postEAreaEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOAreaEmployee dto
    ) {
        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOAreaEmployee answer = objServiceAE.postAreaEmployee(dto, idBusiness);
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
                    "message", "Error al registrar el empleado en el área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/{idAreaEmployee}")
    public ResponseEntity<?> putAreaEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOAreaEmployee dto,
            @PathVariable String idAreaEmployee,
            BindingResult dataResult
    ) {
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DTOAreaEmployee answer = objServiceAE.putAreaEmployee(dto, idAreaEmployee, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al modificar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Empleado en el área modificado correctamente",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al modificar el empleado en el área",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{idAreaEmployee}")
    public ResponseEntity<?> deleteAreaEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idAreaEmployee
    ) {
        try {
            boolean ok = objServiceAE.removeAreaEmployee(idAreaEmployee, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del empleado en el área no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del empleado asignado al área no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Empleado eliminado del área correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el empleado del área",
                    "detail", e.getMessage()
            ));
        }
    }
}
