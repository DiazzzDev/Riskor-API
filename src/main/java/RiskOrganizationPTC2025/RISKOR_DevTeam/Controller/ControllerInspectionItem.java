package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanDetail;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionItem;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceInspectionItem;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/inspectionI")
@Validated
public class ControllerInspectionItem {
    //Inyectamos el Service
    @Autowired
    private ServiceInspectionItem objServiceInspectionI;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getInspectionI")
    public ResponseEntity<Page<DTOInspectionItem>> getInspectionItem(
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

        return ResponseEntity.ok(objServiceInspectionI.getAllInspectionI(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postInspectionI")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postInspectionItem(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOInspectionItem inspectionItem
        ){
        try {
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOInspectionItem objAnswerInspectionI = objServiceInspectionI.postInspectionI(inspectionItem, idBusiness);
            if (objAnswerInspectionI == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Ítem de inspección registrado correctamente, Success",
                    "data", objAnswerInspectionI
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el ítem de inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putInspectionItem/{idInspectionItem}")
    public ResponseEntity<?> putInspectionItem(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOInspectionItem dtoInspectionItem,
            BindingResult dataResult,
            @PathVariable String idInspectionItem
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DTOInspectionItem objAnswerInspectionItem = objServiceInspectionI.putInspectionItem(dtoInspectionItem, idInspectionItem, idBusiness);
            if (objAnswerInspectionItem == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Ítem de inspección modificado correctamente, Success",
                    "data", objAnswerInspectionItem
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el ítem de inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteInspectionItem/{idInspectionItem}")
    public ResponseEntity<?> deleteInspectionItem(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idInspectionItem
        ){
        try {
            boolean ok = objServiceInspectionI.deleteInspectionItem(idInspectionItem, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del ítem de inspección no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del ítem de inspección no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Ítem de inspección eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el ítem de la inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    //CRUD DE LA EVIDENCIA
    @PostMapping("/{idInspectionItem}/evidence/upload")
    public ResponseEntity<?> uploadEvidence(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idInspectionItem,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTOInspectionItem updated = objServiceInspectionI.updateItemEvidence(idBusiness, idInspectionItem, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Evidencia actualizada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La evidencia no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            //Errores de validación de imagen (tamaño, extensión, content-type, etc.)
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al subir la evidencia",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/{idInspectionItem}/evidence")
    public ResponseEntity<?> replaceAreaSketch(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idInspectionItem,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTOInspectionItem updated = objServiceInspectionI.updateItemEvidence(idBusiness, idInspectionItem, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Croquis actualizado correctamente, Success",
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
                    "message", "Error al subir la evidencia",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }

    //Delete de la imágen de los planos del área
    @DeleteMapping("/{idInspectionItem}/evidence")
    public ResponseEntity<?> deleteAreaSketch(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idInspectionItem
    ) {
        try {
            DTOInspectionItem updated = objServiceInspectionI.deleteItemEvidence(idBusiness, idInspectionItem);
            return ResponseEntity.ok(Map.of(
                    "status", "Evidencia eliminada correctamente, Success",
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
                    "message", "Error al eliminar la evidencia",
                    "detail", e.getMessage()
            ));
        }
    }
}
