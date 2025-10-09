package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeControlSafetyDevice;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTypeControlSafetyDevice;
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
@RequestMapping("/api/typeControlSD")
@Validated
public class ControllerTypeControlSafetyDevice {
    //Inyectamos el Service
    @Autowired
    private ServiceTypeControlSafetyDevice objServiceTCSD;

    @GetMapping("/{idTypeControlSD}")
    public ResponseEntity<?> getTypeById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTypeControlSD
        ){
        try {
            if (idTypeControlSD == null || idTypeControlSD.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "idTypeControlSD es requerido"
                ));
            }
            DTOTypeControlSafetyDevice dto = objServiceTCSD.getControlSDSSOById(idTypeControlSD, idBusiness);
            return ResponseEntity.ok(dto);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al obtener el tipo de control de dispositivo de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getTypeControlSD")
    public ResponseEntity<?> getTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ){
        try {
            if (page < 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "page no puede ser negativo"
                ));
            }
            if (size < 1 || size > 30) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "El tamaño de la página debe estar entre 1 y 30"
                ));
            }
            Page<DTOTypeControlSafetyDevice> pageDTO = objServiceTCSD.getAllTypeControlSD(idBusiness, page, size);

            if (pageDTO == null || pageDTO.isEmpty()) {
                // Mantén el shape de Page para el frontend
                return ResponseEntity.ok(Page.empty(org.springframework.data.domain.PageRequest.of(page, size)));
            }
            return ResponseEntity.ok(pageDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar tipos de control de dispositivo de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping("/postTypeControlSD") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTypeControlSafetyDevice dto
    ) {
        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOTypeControlSafetyDevice answer = objServiceTCSD.postTypeControlSD(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Tipo de control de seguridad registrado correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el tipo de control de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/putTypeControlSD/{idTypeControlSD}")
    public ResponseEntity<?> putTypeEPPC(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTypeControlSafetyDevice dto,
            @PathVariable String idTypeControlSD,
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
            DTOTypeControlSafetyDevice answer = objServiceTCSD.putTypeControlSD(dto, idTypeControlSD, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Tipo de control de seguridad modificado correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el tipo de control de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/deletetypeControlSD/{idTypeControlSD}")
    public ResponseEntity<?> deleteTypeControlSD(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTypeControlSD
    ) {
        try {
            boolean ok = objServiceTCSD.removeTypeControlSD(idTypeControlSD, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del tipo de control de seguridad no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del tipo de control de seguridad no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Tipo de control de seguridad eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el tipo de control de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }
}
