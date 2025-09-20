package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOControlSDSSO;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceControlSDSSO;
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
@RequestMapping("/api/ControlSDSSO")
@Validated
public class ControllerControlSDSSO {
    //Inyectamos el Service
    @Autowired
    private ServiceControlSDSSO objServiceControlSDSSO;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getControlSDSSO")
    public ResponseEntity<Page<DTOControlSDSSO>> getControlSDSSO(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        if(size < 0 || size > 40){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 40"
            ));
            return ResponseEntity.ok(null);
        }
        Page<DTOControlSDSSO> devices = objServiceControlSDSSO.getAllControlSDSSO(page, size, idBusiness);
        return ResponseEntity.ok(devices);
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postControlSDSSO")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postControlSDSSO(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOControlSDSSO dtoControlSDSSO
        ){
        try {
            dtoControlSDSSO.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOControlSDSSO objAnswerControlSDSSO = objServiceControlSDSSO.postControlSDSSO(dtoControlSDSSO, idBusiness);
            if (objAnswerControlSDSSO == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Dispositivo de control registrado correctamente, Success",
                    "data", objAnswerControlSDSSO
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el dispositivo de control",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putControlSDSSO/{idServiceDeviceSSO}")
    public ResponseEntity<?> putControlSDSSO(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOControlSDSSO dtoControlSDSSO,
            BindingResult dataResult,
            @PathVariable String idServiceDeviceSSO
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoControlSDSSO.setIdBusiness(idBusiness);
            DTOControlSDSSO objAnswerControlSDSSO = objServiceControlSDSSO.putControlSDSSO(dtoControlSDSSO, idServiceDeviceSSO, idBusiness);
            if (objAnswerControlSDSSO == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Dispositivo de control modificado correctamente, Success",
                    "data", objAnswerControlSDSSO
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el dispositivo de control",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteControlSDSSO/{idServiceDeviceSSO}")
    public ResponseEntity<?> deleteControlSDSSO(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idServiceDeviceSSO
        ){
        try {
            boolean ok = objServiceControlSDSSO.deleteControlSDSSO(idServiceDeviceSSO, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del dispositivo de control SSO no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del dispositivo de control SSO no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Dispositivo de control eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el dispositivo de control",
                    "detail", e.getMessage()
            ));
        }
    }
}