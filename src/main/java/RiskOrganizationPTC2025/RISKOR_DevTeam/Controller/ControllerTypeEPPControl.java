package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeEPPControl;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTypeEPPControl;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/typeEPPC")
@Validated
public class ControllerTypeEPPControl {
    //Inyectamos el Service
    @Autowired
    private ServiceTypeEPPControl objServiceTEPPC;

    @GetMapping("/getTypeEPPC/all")
    public ResponseEntity<List<DTOTypeEPPControl>> getTypeEPPCAll(
            @RequestAttribute("auth.business") String idBusiness
    ){
        List<DTOTypeEPPControl> list = objServiceTEPPC.getAllTypeEPPControlNoP(idBusiness);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{idTypeEPPControl}")
    public DTOTypeEPPControl getEmployeesPositionsById(
            @PathVariable String idTypeEPPControl,
            @RequestAttribute("auth.business") String idBusiness
        ){
        return objServiceTEPPC.getTypeEPPById(idBusiness, idTypeEPPControl);
    }

    //GET PRINCIPAL
    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getTypeEPPC")
    public ResponseEntity<Page<DTOTypeEPPControl>> getTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
        ){
        if(size <= 0 || size > 30){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(objServiceTEPPC.getAllTypeEPPControl(idBusiness, page, size));
    }

    @PostMapping("/postTypeEPPC") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTypeEPPControl dto
        ) {
        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOTypeEPPControl answer = objServiceTEPPC.postTypeEPPC(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Equipo de protección personal registrado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el equipo de protección personal",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putTypeEPPC/{idTypeEPPControl}")
    public ResponseEntity<?> putTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTypeEPPControl dto,
            @PathVariable String idTypeEPPControl,
            BindingResult dataResult
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOTypeEPPControl answer = objServiceTEPPC.putTypeEPPC(dto, idTypeEPPControl, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Equipo de protección personal modificado correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el equipo de protección personal",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteTypeEPPC/{idTypeEPPControl}")
    public ResponseEntity<?> deleteTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTypeEPPControl
        ) {
        try {
            boolean ok = objServiceTEPPC.removeTypeEPPC(idTypeEPPControl, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del equipo de protección personal no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del equipo de protección personal no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Equipo de protección personal eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el equipo de protección personal",
                    "detail", e.getMessage()
            ));
        }
    }
}
