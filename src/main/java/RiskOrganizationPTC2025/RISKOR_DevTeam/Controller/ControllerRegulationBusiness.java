package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceRegulationBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/regulationBusiness")
@Validated
public class ControllerRegulationBusiness {
    @Autowired
    private ServiceRegulationBusiness objServiceRB;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()); // Soporta LocalDate en el dto

    @GetMapping("/getRegulationBusiness")
    public ResponseEntity<?> getRegulationBusiness(
            @RequestAttribute("auth.business") String idBusiness, //Se coloca PathVariable por semántica y evitar problemas de mezcla de datos con el cliente-empresa
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (size <= 0 || size > 40) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 40"
            ));
        }

        return ResponseEntity.ok(objServiceRB.getRegulations(idBusiness, page, size));
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/{idRegulation}/document")
    public ResponseEntity<?> replaceDocument(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idRegulation,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTORegulationBusiness updated = objServiceRB.updateRegulation(idBusiness, idRegulation, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Documento actualizado correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El documento no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al subir el documento",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el documento",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping("/{idRegulation}/document/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idRegulation,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTORegulationBusiness updated = objServiceRB.updateRegulation(idBusiness, idRegulation, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Documento agregada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El documento no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            // Errores de validación de imagen (tamaño, extensión, content-type, etc.)
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al subir el documento",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el documento",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping(
            value = "/postRegulationBusiness",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> postRegulationBusiness(
            @RequestAttribute("auth.business") String idBusiness,
            // A propósito lo recibimos como String para tolerar text/plain o application/json
            @RequestPart("dto") String dtoJson,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // Parseamos el JSON manualmente (tolerante a content-type del part)
            DTORegulationBusiness dto = mapper.readValue(dtoJson, DTORegulationBusiness.class);
            dto.setIdBusiness(idBusiness);

            // El Service ya valida extensión/tamaño y sube a Cloudinary
            DTORegulationBusiness out = objServiceRB.postRegulationBusiness(dto, file, idBusiness);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Regulación empresarial registrada correctamente, Success",
                    "data", out
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Error de validación de datos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar la regulación empresarial",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping(
            value = "/putRegulationBusiness/{idRegulation}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> putRegulationBusinessMultipart(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idRegulation,
            @RequestPart("dto") String dtoJson,                                 // tolera text/plain o app/json
            @RequestPart(value = "file", required = false) MultipartFile file   // PDF opcional
    ) {
        try {
            DTORegulationBusiness dto = mapper.readValue(dtoJson, DTORegulationBusiness.class);
            dto.setIdBusiness(idBusiness);

            DTORegulationBusiness out = objServiceRB.putRegulationBusiness(dto, idRegulation, idBusiness, file);
            return ResponseEntity.ok(Map.of(
                    "status", "Regulación empresarial modificada correctamente, Success",
                    "data", out
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La regulación no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            // Aquí entran también UncheckedIOException y cualquier otro fallo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la regulación empresarial",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/deleteRegulationBusiness/{idRegulation}")
    public ResponseEntity<?> deleteRegulationBusiness(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idRegulation) {
        try {
            boolean ok = objServiceRB.removeRegulationBusiness(idRegulation, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID de la regulación empresarial no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID de la regulación empresarial no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Regulación empresarial eliminada correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la regulación empresarial",
                    "detail", e.getMessage()
            ));
        }
    }

    //Delete de la imágen de los planos del área
    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/{idRegulation}/document")
    public ResponseEntity<?> deleteDocument(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idRegulation
    ) {
        try {
            DTORegulationBusiness updated = objServiceRB.deleteDocument(idBusiness, idRegulation);
            return ResponseEntity.ok(Map.of(
                    "status", "Regulación eliminada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "La regulación no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la regulación del área",
                    "detail", e.getMessage()
            ));
        }
    }
}
