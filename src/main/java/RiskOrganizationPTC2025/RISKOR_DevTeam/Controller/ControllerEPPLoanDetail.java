package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanDetail;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEPPLoanDetail;
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
@RequestMapping("/api/EPPLoanDetail")
@Validated
public class ControllerEPPLoanDetail {
    //Inyectamos el Service
    @Autowired
    private ServiceEPPLoanDetail objServiceEPPLoanD;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getEPPLoanDetail")
    public ResponseEntity<Page<DTOEPPLoanDetail>> getLoanDetail(
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

        return ResponseEntity.ok(objServiceEPPLoanD.getAllEPPLoanDetail(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postEPPLoanDetail")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postLoanDetail(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPLoanDetail eppLoanDetail
        ){
        try {
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOEPPLoanDetail objAnswerEPPLoanD = objServiceEPPLoanD.postEPPLoanDetail(eppLoanDetail, idBusiness);
            if (objAnswerEPPLoanD == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Detalle del préstamo registrado correctamente, Success",
                    "data", objAnswerEPPLoanD
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el detalle del préstamo",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putEPPLoanDetail/{idEPPLoanDetail}")
    public ResponseEntity<?> putLoanDetail(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPLoanDetail dtoEPPLoanDetail,
            BindingResult dataResult,
            @PathVariable String idEPPLoanDetail
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            DTOEPPLoanDetail objAnswerEPPLoanDetail = objServiceEPPLoanD.putEPPLoanDetail(dtoEPPLoanDetail, idEPPLoanDetail, idBusiness);
            if (objAnswerEPPLoanDetail == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Detalle del préstamo modificado correctamente, Success",
                    "data", objAnswerEPPLoanDetail
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el detalle del préstamo",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteEPPLoanDetail/{idEPPLoanDetail}")
    public ResponseEntity<?> deleteLoanDetail(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idEPPLoanDetail
        ){
        try {
            boolean ok = objServiceEPPLoanD.deleteEPPLoanDetail(idEPPLoanDetail, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del detalle del préstamo EPP no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del detalle del préstamo de Equipo de Protección Personal no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Detalle del préstamo de Equipo de Protección Personal eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el detalle del préstamo",
                    "detail", e.getMessage()
            ));
        }
    }
}