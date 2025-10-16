package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOArea;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAreaBundleRequest;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAreaInBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceArea;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
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
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/area")
@Validated
public class ControllerArea {
    @Autowired
    private ServiceArea objServiceA;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping("/getAreaByName")
    public ResponseEntity<?> getAreaByName(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(name = "areaName", required = false) String areaName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        try {
            if (page < 0 || size <= 0 || size > 30) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Parámetros inválidos",
                        "message", "page >= 0 y size entre 1 y 30"
                ));
            }

            Page<DTOArea> result = objServiceA.getAreaByName(idBusiness, areaName, page, size);
            return ResponseEntity.ok(result);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "Área no encontrada",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "Parámetros inválidos",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el área",
                    "detail", e.getMessage()
            ));
        }
    }


    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getArea/{idArea}")
    public ResponseEntity<?> getAreaById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea
    ){
        try {
            if (idArea == null || idArea.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "error", "idArea es requerido"
                ));
            }
            return ResponseEntity.ok(objServiceA.getAreaById(idBusiness, idArea));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Área no encontrada",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasAnyRole('Administrador', 'Mantenimiento')")
    @GetMapping("/getAreas")
    public ResponseEntity<Page<DTOArea>> getAreas(
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

        return ResponseEntity.ok(objServiceA.getAllAreas(idBusiness, page, size));
    }

    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/getAreaBundle/{idArea}")
    public ResponseEntity<?> getAreaBundle(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea) {
        try {
            DTOAreaInBusiness data = objServiceA.getAreaBundle(idBusiness, idArea);
            // mismo envoltorio que el POST
            return ResponseEntity.ok(Map.of(
                    "status", "Área + datos creados correctamente, Success",
                    "data", data
            ));
        }  catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Área no encontrada",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping(value = "/postAreaBundle",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postAreaBundle(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestPart("req") String req,
            @RequestPart(value = "image") MultipartFile photo
    ) {
        try {
            if (photo == null || photo.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status","Datos inválidos",
                        "errorType","VALIDATION_ERROR",
                        "message","El mapa del área (image) es obligatorio"
                ));
            }

            DTOAreaBundleRequest dto = mapper.readValue(req, DTOAreaBundleRequest.class);
            // Seguridad: la empresa SIEMPRE viene del request-attribute
            dto.getArea().setIdBusiness(idBusiness);

            DTOAreaInBusiness out = objServiceA.postAreaBundle(idBusiness, dto, photo);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Área + datos creados correctamente, Success",
                    "data", out
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
                    "message", "Error al registrar el bundle de área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping(value = "/putAreaBundle/{idArea}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putAreaBundle(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea,
            @RequestPart("req") String req,
            @RequestPart(value = "image", required = false) MultipartFile photo
    ) {
        try {
            // parse JSON
            DTOAreaBundleRequest dto = mapper.readValue(req, DTOAreaBundleRequest.class);

            // seguridad: forzamos idBusiness
            dto.getArea().setIdBusiness(idBusiness);
            // aseguramos que el idArea venga por path variable (preferible)
            dto.getArea().setIdArea(idArea);

            DTOAreaInBusiness out = objServiceA.putAreaBundle(idBusiness, idArea, dto, photo);

            return ResponseEntity.ok(Map.of(
                    "status", "Área + datos actualizados correctamente, Success",
                    "data", out
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", e.getMessage(),
                    "timeStamp", Instant.now().toString()
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
                    "message", "Error al actualizar el bundle de área",
                    "detail", e.getMessage()
            ));
        }
    }

    /*
    @PreAuthorize("hasRole('Administrador')")
    @PostMapping("/postArea") //Usar ResponseEntity<?> permite una flexibilidad al momento de las respuestas HTTP
    public ResponseEntity<?> postArea(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOArea dto) {
        try {
            //Forzamos empresa del path (evita que la cambien en el body) - Tema de seguridad
            dto.setIdBusiness(idBusiness);
            DTOArea answer = objServiceA.postArea(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Área registrada correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el área",
                    "detail", e.getMessage()
            ));
        }
    }

     */

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping("/{idArea}/sketch/upload")
    public ResponseEntity<?> uploadAreaSketch(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTOArea updated = objServiceA.updateAreaSketch(idBusiness, idArea, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Croquis agregada correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El área no pertenece a esta empresa o no existe",
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
                    "message", "Error al subir el croquis del área",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el croquis del área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/putArea/{idArea}")
    public ResponseEntity<?> putArea(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOArea dto,
            @PathVariable String idArea,
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
            DTOArea answer = objServiceA.putArea(dto, idArea, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Área modificada correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/{idArea}/sketch")
    public ResponseEntity<?> replaceAreaSketch(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            DTOArea updated = objServiceA.updateAreaSketch(idBusiness, idArea, image);
            return ResponseEntity.ok(Map.of(
                    "status", "Croquis actualizado correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El área no pertenece a esta empresa o no existe",
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
                    "message", "Error al subir el croquis del área",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el croquis del área",
                    "detail", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/deleteArea/{idArea}")
    public ResponseEntity<?> deleteArea(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea) {
        try {
            boolean ok = objServiceA.removeArea(idArea, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "Área no encontrada").body(Map.of(
                        "status", "No encontrado",
                        "message", "El área no pertenece a esta empresa o no existe",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Área eliminada correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el área",
                    "detail", e.getMessage()
            ));
        }
    }

    //Delete de la imágen de los planos del área
    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/{idArea}/sketch")
    public ResponseEntity<?> deleteAreaSketch(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idArea
    ) {
        try {
            DTOArea updated = objServiceA.deleteAreaSketch(idBusiness, idArea);
            return ResponseEntity.ok(Map.of(
                    "status", "Croquis eliminado correctamente, Success",
                    "data", updated
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado",
                    "message", "El área no pertenece a esta empresa o no existe",
                    "detail", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el croquis del área",
                    "detail", e.getMessage()
            ));
        }
    }
}