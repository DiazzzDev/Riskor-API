package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCustomRiskAssoci;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceCustomRiskAssoci;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customRiskAssoci")
public class ControllerCustomRiskAssoci {
    @Autowired
    private ServiceCustomRiskAssoci objServiceCRA;

    @GetMapping("/getRisk")
    public List<DTOCustomRiskAssoci> getRisk(@RequestAttribute("auth.business") String idBusiness){
        return objServiceCRA.getAllRisks(idBusiness);
    }

    @GetMapping("/getRisk/title/{idCustomTitleInsp}")
    public List<DTOCustomRiskAssoci> getRiskByTitle(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idCustomTitleInsp
        ){
        return objServiceCRA.getAllRisksByTitle(idBusiness, idCustomTitleInsp);
    }

    @PostMapping("/postRisk") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postRisk(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOCustomRiskAssoci dto) {
        try {
            dto.setIdBusiness(idBusiness);
            DTOCustomRiskAssoci answer = objServiceCRA.postRisk(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Riesgo asociado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el riesgo asociado",
                    "detail", e.getMessage()
            ));
        }
    }

    @PatchMapping("/patchRisk/{idCustomRiskAssoci}")
    public ResponseEntity<?> patchRisk(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOCustomRiskAssoci dto,
            @PathVariable String idCustomRiskAssoci,
            BindingResult dataResult
    ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            //Empresa del área mandada en el path
            dto.setIdBusiness(idBusiness);
            DTOCustomRiskAssoci answer = objServiceCRA.patchRisk(dto, idCustomRiskAssoci, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Riesgo asociado modificada correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el riesgo",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteRisk/{idCustomRiskAssoci}")
    public ResponseEntity<?> deleteRisk(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idCustomRiskAssoci) {
        try {
            boolean ok = objServiceCRA.removeRisk(idCustomRiskAssoci, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "título no encontrado").body(Map.of(
                        "status", "No encontrado",
                        "message", "El riesgo de inspección no pertenece a esta empresa o no existe",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Riesgo eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el riesgo",
                    "detail", e.getMessage()
            ));
        }
    }
}
