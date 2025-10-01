package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataDuplicate;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceBusinessInfo;
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
@RequestMapping("/api/businessInfo")
@Validated
public class ControllerBusinessInfo {
    @Autowired
    private ServiceBusinessInfo objServiceBI;

    @GetMapping("/getMyBusiness")
    public DTOBusinessInfo getBusinessById(@RequestAttribute("auth.business") String idBusiness){
        return objServiceBI.getBusinessById(idBusiness);
    }

    @PostMapping("/postBusinessInfo") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postBusinessInfo(@Valid @RequestBody DTOBusinessInfo dto) { //Requestbody transforma el valor obtenido a un obj de Java //Valid se encarga de validar un obj recibido en el controller
        try {
            DTOBusinessInfo answer = objServiceBI.insertBusinessInfo(dto);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Negocio registrado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el negocio",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putBusinessInfo/{idBusiness}")
    public ResponseEntity<?> putBusinessInfo(@Valid @RequestBody DTOBusinessInfo dto, @PathVariable String idBusiness, BindingResult dataResult) {

        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DTOBusinessInfo answer = objServiceBI.putBusinessInfo(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Negocio modificado correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (ExceptionDataDuplicate e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "Error", "Datos duplicados, intentelo denuevo",
                    "Campo duplicado: ", e.getDuplicateData()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el negocio",
                    "detail", e.getMessage()
            ));
        }
    }

    //Preguntar si lo borro o armo trigger
    @DeleteMapping("/deleteBusinessInfo/{idBusiness}")
    public ResponseEntity<?> deleteBusinessInfo(@Valid @PathVariable String idBusiness) {
        try {
            if (!objServiceBI.removeBusinessInfo(idBusiness)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del negocio no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del negocio no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Negocio eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el negocio",
                    "detail", e.getMessage()
            ));
        }
    }
}
