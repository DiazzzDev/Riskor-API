package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOLocation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceLocation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
@Validated
public class ControllerLocation {
    @Autowired
    private ServiceLocation objServiceL;

    @GetMapping("/getLocations") //Response entity<?> Es una forma flexible de lo que vamos a mostrar en la respuesta HTTP
    public ResponseEntity<?> getLocations(@RequestAttribute("auth.business") String idBusiness) {
        try {
            List<DTOLocation> list = objServiceL.getLocation(idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar locaciones",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postLocation")
    public ResponseEntity<?> postLocation(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOLocation location
    ) {
        try {
            // Forzamos empresa desde la ruta (seguridad multiempresa)
            location.setIdBusiness(idBusiness);
            DTOLocation saved = objServiceL.postLocation(location, idBusiness);

            if (saved == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Locación registrada correctamente, Success",
                    "data", saved
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            // FK/UK: área no existe, nombre duplicado (si hay UK), etc.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "Violación de integridad de datos (FK/UK)"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar la locación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putLocation/{idLocation}")
    public ResponseEntity<?> putLocation(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOLocation dto,
            BindingResult dataResult,                   // << debe ir justo después del @Valid
            @PathVariable String idLocation
        ) {
        // Validación del body (@Valid)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Errores de validación",
                    "errors", errors
            ));
        }
        try {
            dto.setIdBusiness(idBusiness); // seguridad multiempresa
            DTOLocation answer = objServiceL.putLocation(dto, idLocation, idBusiness);

            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Locación modificada correctamente, Success",
                    "data", answer
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", e.getMessage() != null ? e.getMessage() : "No se encontró la ubicación para esta empresa"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of( //409
                    "status", "Conflicto",
                    "message", "Violación de integridad de datos (FK/UK)"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of( //500
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la locación",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteLocation/{idLocation}")
    public ResponseEntity<?> deleteLocation(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idLocation
    ) {
        try {
            boolean ok = objServiceL.removeLocation(idLocation, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                                "Error, ID no encontrado", "ID de la locación no encontrado")
                        .body(Map.of(
                                "status", "No encontrado, Error",
                                "message", "El ID de la locación no ha sido encontrado",
                                "timeStamp", Instant.now().toString()
                        ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Locación eliminada correctamente, Success"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of( //404
                    "status", "No encontrado, Error",
                    "message", e.getMessage() != null ? e.getMessage() : "No se encontró la ubicación para esta empresa"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of( //400
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) { //409
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "Conflicto",
                    "message", "No se puede eliminar: registro referenciado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la locación",
                    "detail", e.getMessage()
            ));
        }
    }
}