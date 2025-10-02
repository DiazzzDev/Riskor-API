package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataDuplicate;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOUser;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
@RequestMapping("/api/users")
@Validated //Habilita validación para @RequestParam/@PathVariable
public class ControllerUser {
    @Autowired
    private ServiceUser objServiceUsers;

    @PostMapping("/postUser")
    public ResponseEntity<?> postUser(@Valid @RequestBody DTOUser dto){
        try{
            DTOUser answerU = objServiceUsers.postUser(dto);
            if(answerU == null){
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Usuario creado correctamente, Success",
                    "data", answerU
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el usuario",
                    "detail", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{username}/status")
    public ResponseEntity<?> patchUserStatus(
            @PathVariable String username,
            @RequestParam @Pattern(regexp = "[TF]", message = "Estatus debe ser 'T' o 'F'") String value
    ){
        try{
            DTOUser user = objServiceUsers.patchUserStatus(username, value);
            return ResponseEntity.ok(Map.of(
                    "status", "Usuario modificado correctamente",
                    "username", user.getUsername(),
                    "newStatus", user.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Error al actualizar los datos",
                    "errorType", "VALIDATION_ERROR",
                    "message", "Datos inválidos, vuelva a intentarlo"
            ));
        }
    }
}
