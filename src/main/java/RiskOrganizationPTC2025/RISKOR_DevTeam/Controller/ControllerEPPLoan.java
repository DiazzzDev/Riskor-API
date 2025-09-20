package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEPPLoan;
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
@RequestMapping("/api/EPPLoan")
@Validated
public class ControllerEPPLoan {
    //Inyectamos el Service
    @Autowired
    private ServiceEPPLoan objServiceEPPLoan;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getEPPLoan")
    public ResponseEntity<Page<DTOEPPLoan>> getLoan(
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

        return ResponseEntity.ok(objServiceEPPLoan.getAllEPPLoan(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postEPPLoan")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postLoan(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPLoan dtoEPPLoan
        ){
        try {
            dtoEPPLoan.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOEPPLoan objAnswerEPPL = objServiceEPPLoan.postEPPLoan(dtoEPPLoan, idBusiness);
            if (objAnswerEPPL == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Préstamo registrado correctamente, Success",
                    "data", objAnswerEPPL
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el préstamo",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putEPPLoan/{idEPPLoan}")
    public ResponseEntity<?> putLoan(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPLoan dtoEPPLoan,
            BindingResult dataResult,
            @PathVariable String idEPPLoan
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoEPPLoan.setIdBusiness(idBusiness);
            DTOEPPLoan objAnswerEPPLoan = objServiceEPPLoan.putEPPLoan(dtoEPPLoan, idEPPLoan, idBusiness);
            if (objAnswerEPPLoan == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Préstamo de equipo de protección personal modificado correctamente, Success",
                    "data", objAnswerEPPLoan
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el préstamo",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteEPPLoan/{idEPPLoan}")
    public ResponseEntity<?> deleteLoan(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idEPPLoan
        ){
        try {
            boolean ok = objServiceEPPLoan.deleteEPPLoan(idEPPLoan, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del Prestamo EPP no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del Prestamo EPP no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Préstamo de equipo de protección personal eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el préstamo",
                    "detail", e.getMessage()
            ));
        }
    }
}
