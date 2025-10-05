package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspection;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceInspection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inspection")
@Validated
@PreAuthorize("hasAnyRole('Administrador', 'Mantenimiento')")
public class ControllerInspection {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()); // Soporta LocalDate en el dto

    //Inyectamos el Service
    @Autowired
    private ServiceInspection objServiceInspection;

    @GetMapping("/getInspections/{idInspection}")
    public ResponseEntity<?> getById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idInspection
    ){
        return ResponseEntity.ok(objServiceInspection.getInspectionById(idBusiness, idInspection));
    }

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getInspections")
    public ResponseEntity<Page<DTOInspection>> getTypeEPPC(
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
        return ResponseEntity.ok(objServiceInspection.getAllInspection(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping(
            value = "/postInspection",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    //RequestPart indica que se debe enviar como FORM DATA
    public ResponseEntity<?> postInspection(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestPart("dto") DTOInspection dto,
            @RequestPart(value = "file", required = false) MultipartFile file
        ){
        try {
            dto.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOInspection objAnswerI = objServiceInspection.postInspection(dto, idBusiness, file);
            if (objAnswerI == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Inspección registrada correctamente, Success",
                    "data", objAnswerI
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar la inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putInspection/{idInspection}")
    public ResponseEntity<?> putInspection(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOInspection dtoInspection,
            BindingResult dataResult,
            @PathVariable String idInspection
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoInspection.setIdBusiness(idBusiness);
            DTOInspection objAnswerInspection = objServiceInspection.putInspection(dtoInspection, idInspection, idBusiness);
            if (objAnswerInspection == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Inspección modificada correctamente, Success",
                    "data", objAnswerInspection
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la inspección",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteInspection/{idInspection}")
    public ResponseEntity<?> deleteData(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idInspection
        ){
        try {
            boolean ok = objServiceInspection.deleteInspection(idInspection, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID de la inspección no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID de la inspección no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Inspección eliminada correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la inspección",
                    "detail", e.getMessage()
            ));
        }
    }
}
