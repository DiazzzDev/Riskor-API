package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentBodyPart;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/accidentBodyP")
@Validated
public class ControllerAccidentBodyPart {
    @Autowired
    private ServiceAccidentBodyPart objServiceABP;

    @GetMapping("/getAccidentBodyP")
    public ResponseEntity<?> getData(@RequestAttribute("auth.business") String idBusiness) {
        try {
            List<DTOAccidentBodyPart> list = objServiceABP.getAllAccidentBodyP(idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar partes del cuerpo dañadas",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postAccidentBodyP")
    public ResponseEntity<?> postData(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOAccidentBodyPart accidentBodyPart,
            BindingResult dataResult) {

        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DTOAccidentBodyPart answer = objServiceABP.postAccidentBodyP(accidentBodyPart, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Partes del cuerpo dañadas registradas correctamente, Success",
                    "data", answer
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al guardar los datos ingresados",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteAccidentBodyP/{idAccidentBodyPart}")
    public ResponseEntity<?> delete(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idAccidentBodyPart) {

        try {
            boolean ok = objServiceABP.removeAccidentBodyP(idAccidentBodyPart, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID de la parte del cuerpo dañada no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID de la parte del cuerpo dañada no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Parte del cuerpo dañada eliminada correctamente, Success"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", e.getMessage(),
                    "timeStamp", Instant.now().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el registro",
                    "detail", e.getMessage()
            ));
        }
    }
}