package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEvidence;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEvidence;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evidence")
@Validated
public class ControllerEvidence {
    @Autowired
    private ServiceEvidence objServiceEvidence;

    @GetMapping("/getEvidence")
    public ResponseEntity<?> getEvidence(@RequestAttribute("auth.business") String idBusiness){
        try {
            List<DTOEvidence> list = objServiceEvidence.getAllEvidence(idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar evidencias",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postEvidence")
    public ResponseEntity<?> postEvidence(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEvidence evidence,
            BindingResult dataResult){
        if (dataResult.hasErrors()){
            Map<String,String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {

            DTOEvidence answer = objServiceEvidence.postEvidence(evidence, idBusiness);
            if (answer == null){
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Evidencia del accidente registrada correctamente, Success",
                    "data", answer
            ));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Validación",
                    "message", ex.getMessage()
            ));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar la evidencia del accidente",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putEvidence/{idEvidence}")
    public ResponseEntity<?> putEvidence(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEvidence dtoEvidence,
            @PathVariable String idEvidence,
            BindingResult dataResult){

        if (dataResult.hasErrors()){
            Map<String,String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            DTOEvidence answer = objServiceEvidence.putEvidence(dtoEvidence, idEvidence, idBusiness);
            if (answer == null){
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Evidencia del accidente modificada correctamente, Success",
                    "data", answer
            ));
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", e.getMessage(),
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la evidencia del accidente",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteEvidence/{idEvidence}")
    public ResponseEntity<?> deleteEvidence(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEvidence){
        try {
            boolean ok = objServiceEvidence.deleteEvidence(idEvidence, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID de la evidencia no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID de la evidencia no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Evidencia eliminada correctamente, Success"
            ));
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", e.getMessage(),
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }

    //CRUD DE CLOUDINARY
    //Subir / primera carga (form-data: image)
    @PostMapping("/{idEvidence}/image/upload")
    public ResponseEntity<?> uploadEvidenceImage(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEvidence,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            // Usa el mismo método para “upsert” (sobrescribe si existe)
            DTOEvidence updated = objServiceEvidence.updateEvidence(idBusiness, idEvidence, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Imagen de evidencia actualizada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La evidencia no pertenece a esta empresa o no existe",
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
                    "message", "Error al subir la imagen de la evidencia",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la imagen de la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }

    //Reemplazar imagen A MOSTRAR PARA EL CLIENTE (form-data: image)
    @PutMapping("/{idEvidence}/image")
    public ResponseEntity<?> replaceEvidenceImage(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEvidence,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTOEvidence updated = objServiceEvidence.updateEvidence(idBusiness, idEvidence, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Imagen de evidencia actualizada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La evidencia no pertenece a esta empresa o no existe",
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
                    "message", "Error al subir la imagen de la evidencia",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la imagen de la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }

    //Eliminar imagen (borra en Cloudinary y limpia/placeholder en DB)
    @DeleteMapping("/{idEvidence}/image")
    public ResponseEntity<?> deleteEvidenceImage(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEvidence
    ) {
        try {
            DTOEvidence updated = objServiceEvidence.deleteEvidenceImage(idBusiness, idEvidence);
            return ResponseEntity.ok(Map.of(
                    "status", "Imagen de evidencia eliminada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La evidencia no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la imagen de la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }
}