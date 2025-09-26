package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTraining;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTraining;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("/api/training")
@Validated
public class ControllerTraining {
    @Autowired
    private ServiceTraining objServiceT;

    @GetMapping("/getTraining/getTrainingByTitle/{title}")
    public ResponseEntity<?> getTrainingByTitle(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        try {
            if (size < 5 || size > 30) {
                ResponseEntity.badRequest().body(Map.of(
                        "status", "El tamaño de la página debe estar entre 5 y 30"
                ));
                return ResponseEntity.ok(null);
            }
            Page<DTOTraining> trainings = objServiceT.getTrainingByTitle(page, size, title, idBusiness);

            return ResponseEntity.ok(trainings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Datos inválidos",
                    "errorType", "VALIDATION_ERROR",
                    "message", "Datos inválidos, vuelva a intentarlo"
            ));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar capacitaciones",
                    "detail", e.getMessage()
            ));
        }
    }

    //Método para obtener todas las capacitaciones de un empleado
    @GetMapping("/employee/{idEmployee}")
    public ResponseEntity<List<DTOTraining>> getTrainingsByEmployee(
            @PathVariable String idEmployee,
            @RequestAttribute("auth.business") String idBusiness
    ) {
        List<DTOTraining> trainings = objServiceT.getTrainingsByEmployee(idEmployee, idBusiness);
        if (trainings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/getTraining/{idTraining}")
    public ResponseEntity<?> getTrainingById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTraining
    ){
        try {
            DTOTraining training = objServiceT.getTrainingById(idTraining, idBusiness);
            return ResponseEntity.ok(training);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @GetMapping("/getTraining")
    public ResponseEntity<?> getTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        try {
            if (size < 5 || size > 30) {
                ResponseEntity.badRequest().body(Map.of(
                        "status", "El tamaño de la página debe estar entre 5 y 30"
                ));
                return ResponseEntity.ok(null);
            }
            Page<DTOTraining> trainings = objServiceT.getAllTrainings(page, size, idBusiness);

            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar capacitaciones",
                    "detail", e.getMessage()
            ));
        }
    }

    @PostMapping("/postTraining")
    public ResponseEntity<?> postTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTraining dto
    ) {
        try {
            dto.setIdBusiness(idBusiness);
            DTOTraining answer = objServiceT.postTraining(dto, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Capacitación creada correctamente, Success",
                    "data", answer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putTraining/{idTraining}")
    public ResponseEntity<?> putTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOTraining dto,
            @PathVariable String idTraining,
            BindingResult dataResult
    ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dto.setIdBusiness(idBusiness);
            DTOTraining answer = objServiceT.putTraining(dto, idTraining, idBusiness);
            if (answer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Capacitación modificada correctamente, Success",
                    "data", answer
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al modificar la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteTraining/{idTraining}")
    public ResponseEntity<?> deleteTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTraining
    ){
        try {
            boolean ok = objServiceT.removeTraining(idTraining, idBusiness);
            if (!ok) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID de la capacitación no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID de la capacitación no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Capacitación eliminada correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la capacitación",
                    "detail", e.getMessage()
            ));
        }
    }
}
