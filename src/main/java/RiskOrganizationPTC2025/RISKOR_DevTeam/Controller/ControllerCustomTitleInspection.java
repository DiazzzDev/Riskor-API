package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCustomTitleInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceCustomTitleInspection;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('Gerente', 'Mantenimiento')")
@RequestMapping("/api/customTitleInspection")
public class ControllerCustomTitleInspection {
    @Autowired
    private ServiceCustomTitleInspection objServiceCTI;

    @GetMapping("/getTitles")
    public List<DTOCustomTitleInspection> getTitles(@RequestAttribute("auth.business") String idBusiness){
        return objServiceCTI.getAllTitles(idBusiness);
    }

    @PostMapping("/postTitle") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postTitle(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOCustomTitleInspection dto) {
        try {
            dto.setIdBusiness(idBusiness);
            DTOCustomTitleInspection answer = objServiceCTI.postTitle(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Título registrado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el título de inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @PatchMapping("/patchTitle/{idCustomTitleInsp}")
    public ResponseEntity<?> patchTitle(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOCustomTitleInspection dto,
            @PathVariable String idCustomTitleInsp,
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
            DTOCustomTitleInspection answer = objServiceCTI.patchTitle(dto, idCustomTitleInsp, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Título modificada correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el título",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteTitle/{idCustomTitleInsp}")
    public ResponseEntity<?> deleteTitle(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idCustomTitleInsp) {
        try {
            boolean ok = objServiceCTI.removeTitle(idCustomTitleInsp, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "título no encontrado").body(Map.of(
                        "status", "No encontrado",
                        "message", "El título de inspección no pertenece a esta empresa o no existe",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Título eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el título",
                    "detail", e.getMessage()
            ));
        }
    }
}
