package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingRating;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTrainingRating;
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
@RequestMapping("/api/trainingRating")
@Validated
public class ControllerTrainingRating {
    @Autowired
    private ServiceTrainingRating objServiceTR;

    @GetMapping("/getTrainingRating/getIdTrainingRating/{idTrainingRating}")
    public ResponseEntity<?> getTrainingRatingById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTrainingRating
    ) {
        try {
            DTOTrainingRating dto = objServiceTR.rating(idTrainingRating, idBusiness);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Calificación no encontrada",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar la calificación",
                    "detail", e.getMessage()
            ));
        }
    }

    @GetMapping("/getTrainingRating/trainingEmployee/{idTrainingEmployee}")
    public ResponseEntity<?> getTrainingRating(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTrainingEmployee
        ){
        try {
            if (idTrainingEmployee == null || idTrainingEmployee.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "idTrainingEmployee es requerido"
                ));
            }
            List<DTOTrainingRating> list = objServiceTR.listByTrainingEmployee(idTrainingEmployee, idBusiness);
            return ResponseEntity.ok(list);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", e.getMessage() // "Registro de participación en capacitación no encontrado"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar calificaciones por empleado en capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postTrainingRating")
    public ResponseEntity<?> postTrainingRating(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTrainingRating dtoTR
        ){
        try {
            //idBusiness va por ruta; En este apartado lo fijamos desde el service
            DTOTrainingRating saved = objServiceTR.postTrainingRating(dtoTR, idBusiness);
            if (saved == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Calificación de capacitación registrada correctamente, Success",
                    "data", saved
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
                    "message", "Error al registrar la calificación de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putTrainingRating/{idTrainingRating}")
    public ResponseEntity<?> putTrainingRating(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTrainingRating dto,
            BindingResult dataResult,                // << debe ir inmediatamente después del @Valid
            @PathVariable String idTrainingRating
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if(dataResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            //idBusiness va por ruta; En este apartado lo fijamos desde el service
            DTOTrainingRating updated = objServiceTR.putTrainingRating(dto, idTrainingRating, idBusiness);
            if (updated == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Calificación de capacitación modificada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Calificación no encontrada"
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
                    "message", "Error al actualizar la calificación de la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }
    /*
    @DeleteMapping("/deleteTrainingRating/{idTrainingRating}")
    public ResponseEntity<?> deleteArea(@Valid @PathVariable String idTrainingRating){
        try{
            if (!objServiceTR.removeTrainingRating(idTrainingRating)){
                return ResponseEntity.badRequest().body("El ID de la calificación no puede ser nulo o vacío");
            }
            return ResponseEntity.ok("Calificación eliminada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la calificación: " + e.getMessage());
        }
    }*/
}
