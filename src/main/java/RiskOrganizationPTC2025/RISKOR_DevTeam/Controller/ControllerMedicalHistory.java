package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMedicalHistory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceMedicalHistory;
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
@RequestMapping("/api/medicalHistory")
@Validated
public class ControllerMedicalHistory {
    //Inyectamos el Service
    @Autowired
    private ServiceMedicalHistory objServiceMedicalH;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getMedicalH")
    public ResponseEntity<?> getHistory(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        if (size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "El tamaño de la página debe estar entre 1 y 50"
            ));
        }
        return ResponseEntity.ok(objServiceMedicalH.getAllMedicalH(page, size, idBusiness));
    }

    //GET paginado por expediente médico (dentro de la empresa)
    @GetMapping("/record/{idMedicalRecord}")
    public ResponseEntity<?> getByRecord(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idMedicalRecord,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        if (size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "El tamaño de la página debe estar entre 1 y 50"
            ));
        }
        return ResponseEntity.ok(objServiceMedicalH.getByMedicalRecord(idMedicalRecord, page, size, idBusiness));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postMedicalH")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postHistory(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOMedicalHistory dtoMedicalHistory
        ){
        try {
            dtoMedicalHistory.setIdBusiness(idBusiness); //Tomamos la empresa desde el path, no desde el
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOMedicalHistory objAnswerMedicalH = objServiceMedicalH.postMedicalHistory(dtoMedicalHistory,idBusiness);
            if (objAnswerMedicalH == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Historial médico registrado correctamente, Success",
                    "data", objAnswerMedicalH
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el historial médico",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putMedicalH/{idMedicalHistory}")
    public ResponseEntity<?> putHistory(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOMedicalHistory dtoMedicalHistory,
            BindingResult dataResult,
            @PathVariable String idMedicalHistory
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoMedicalHistory.setIdBusiness(idBusiness);
            DTOMedicalHistory objAnswerMedicalHistory = objServiceMedicalH.putMedicalHistory(dtoMedicalHistory, idMedicalHistory, idBusiness);
            if (objAnswerMedicalHistory == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Historial médico modificado correctamente, Success",
                    "data", objAnswerMedicalHistory
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el historial médico",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteMedicalH/{idMedicalHistory}")
    public ResponseEntity<?> deleteHistory(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idMedicalHistory
        ){
        try {
            boolean ok = objServiceMedicalH.deleteMedicalHistory(idMedicalHistory, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del historial médico no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del historial médico no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Historial médico eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el historial médico",
                    "detail", e.getMessage()
            ));
        }
    }
}