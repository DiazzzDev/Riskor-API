package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMaintenanceControlSD;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceMaintenanceControlSD;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/MaintenanceCSD")
@Validated
@PreAuthorize("hasAnyRole('Administrador', 'Mantenimiento')")
public class ControllerMaintenanceControlSD {
    //Inyectamos el Service
    @Autowired
    private ServiceMaintenanceControlSD objServiceMaintenanceCSD;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getMaintenanceCSD")
    public ResponseEntity<Page<DTOMaintenanceControlSD>> getMaintenanceCSD(
            @RequestAttribute("auth.business") String idBusiness, //Se coloca PathVariable por semántica y evitar problemas de mezcla de datos con el cliente-empresa
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ){
        if(size < 0 || size > 30){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(objServiceMaintenanceCSD.getAllMaintenanceCSD(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postMaintenanceCSD")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postMaintenanceCSD(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOMaintenanceControlSD dtoMaintenanceControlSD
        ){
        try {
            dtoMaintenanceControlSD.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOMaintenanceControlSD objAnswerMaintenanceC = objServiceMaintenanceCSD.postMaintenanceC(dtoMaintenanceControlSD, idBusiness);
            if (objAnswerMaintenanceC == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Mantenimiento registrado correctamente, Success",
                    "data", objAnswerMaintenanceC
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el mantenimiento",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putMaintenanceSD/{idMaintenanceControlSD}")
    public ResponseEntity<?> putMaintenanceCSD(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOMaintenanceControlSD dtoMaintenanceControlSD,
            BindingResult dataResult,
            @PathVariable String idMaintenanceControlSD
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoMaintenanceControlSD.setIdBusiness(idBusiness);
            DTOMaintenanceControlSD objAnswerMaintenanceSD = objServiceMaintenanceCSD.putMaintenanceControlSD(dtoMaintenanceControlSD, idMaintenanceControlSD, idBusiness);
            if (objAnswerMaintenanceSD == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Mantenimiento modificado correctamente, Success",
                    "data", objAnswerMaintenanceSD
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el mantenimiento",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteMaintenanceSD/{idMaintenanceControlSD}")
    public ResponseEntity<?> deleteMaintenanceCSD(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idMaintenanceControlSD
        ){
        try {
            boolean ok = objServiceMaintenanceCSD.deleteMaintenanceControlSD(idMaintenanceControlSD, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del mantenimiento de control de seguridad no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del mantenimiento de control de seguridad no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Mantenimiento de control de seguridad eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el mantenimiento",
                    "detail", e.getMessage()
            ));
        }
    }
}
