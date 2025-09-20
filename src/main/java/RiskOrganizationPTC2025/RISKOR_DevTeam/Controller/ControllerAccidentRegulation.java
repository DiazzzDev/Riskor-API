package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentRegulation;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentRegulation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accidentRegulation")
@Validated
public class ControllerAccidentRegulation {
    @Autowired
    private ServiceAccidentRegulation objServiceAccidentR;

    @GetMapping("/getAccidentRegulation")
    public ResponseEntity<?> getData(@RequestAttribute("auth.business") String idBusiness){
        try {
            List<DTOAccidentRegulation> list = objServiceAccidentR.getAllAccidentR(idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar relaciones accidente–regulación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postAccidentRegulation")
    public ResponseEntity<?> postData(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOAccidentRegulation accidentRegulation,
            BindingResult dataResult) {

        if (dataResult.hasErrors()){
            Map<String,String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            DTOAccidentRegulation saved = objServiceAccidentR.postAccidentR(accidentRegulation, idBusiness);
            if (saved == null){
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Se ha registrado el accidente con la regulación correspondiente, Success",
                    "data", saved
            ));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al guardar los datos ingresados",
                    "detail", e.getMessage()
            ));
        }
    }
}