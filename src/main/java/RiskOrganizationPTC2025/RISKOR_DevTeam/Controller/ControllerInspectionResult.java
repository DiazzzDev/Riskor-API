package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionResult;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceInspectionResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inspectionR")
@Validated
public class ControllerInspectionResult {
    //Inyectamos el Service
    @Autowired
    private ServiceInspectionResult objServiceInspectionR;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getInspectionR")
    public ResponseEntity<Page<DTOInspectionResult>> getResult(
            @RequestAttribute("auth.business") String idBusiness, //Se coloca PathVariable por semántica y evitar problemas de mezcla de datos con el cliente-empresa
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
        ){
        if(size <= 0 || size > 30){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(objServiceInspectionR.getAllInspectionR(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postInspectionR")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postResult(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOInspectionResult dtoInspectionResult
        ){
        try {
            dtoInspectionResult.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicaran la respuesta dirigiendose al Service, recibiendo como parámetros los valores de los campos
            DTOInspectionResult objAnswerInspectionR = objServiceInspectionR.postInspectionR(dtoInspectionResult, idBusiness);
            if (objAnswerInspectionR == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Resultado de inspección registrado correctamente, Success",
                    "data", objAnswerInspectionR
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el resultado de inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putInspectionR/{idInspectionResult}")
    public ResponseEntity<?> putResult(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOInspectionResult dtoInspectionResult,
            BindingResult dataResult,
            @PathVariable String idInspectionResult
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoInspectionResult.setIdBusiness(idBusiness);
            DTOInspectionResult objAnswerInspectionResult = objServiceInspectionR.putInspectionResult(dtoInspectionResult, idInspectionResult, idBusiness);
            if (objAnswerInspectionResult == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Resultado de inspección modificado correctamente, Success",
                    "data", objAnswerInspectionResult
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el resultado de inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteInspectionR/{idInspectionResult}")
    public ResponseEntity<?> deleteResult(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idInspectionResult
        ){
        try {
            boolean ok = objServiceInspectionR.deleteInspectionResult(idInspectionResult, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del resultado de inspección no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del resultado de inspección no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Resultado de inspección eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el resultado de la inspección",
                    "detail", e.getMessage()
            ));
        }
    }
}
