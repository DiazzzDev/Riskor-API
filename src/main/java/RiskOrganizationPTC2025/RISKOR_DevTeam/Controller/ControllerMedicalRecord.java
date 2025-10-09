package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataDuplicate;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMedicalRecord;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceMedicalRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/medicalRecord")
@Validated
public class ControllerMedicalRecord {
    //Inyectamos el Service
    @Autowired
    private ServiceMedicalRecord objServiceMedicalR;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getMedicalRecord")
    public ResponseEntity<?> getAll(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
        ){
        if (size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "El tamaño de la página debe estar entre 1 y 50"
            ));
        }
        return ResponseEntity.ok(objServiceMedicalR.getAllMedicalR(page, size, idBusiness));
    }

    //GET paginado por empleado
    @GetMapping("/employee/{idEmployee}")
    public ResponseEntity<?> getByEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
        ){
        try {
            if (idEmployee == null || idEmployee.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "error", "idEmployee es requerido"
                ));
            }
            if (size <= 0 || size > 50) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "VALIDATION_ERROR",
                        "message", "El tamaño de la página debe estar entre 1 y 50"
                ));
            }
            return ResponseEntity.ok(objServiceMedicalR.getMedicalRByEmployee(idEmployee, page, size, idBusiness));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Registro médico no encontrado",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el registro médico",
                    "detail", e.getMessage()
            ));
        }
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postMedicalRecord")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postMedicalRecord(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOMedicalRecord medicalRecord
        ){
        try {
            medicalRecord.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOMedicalRecord objAnswerMedicalR = objServiceMedicalR.postMedicalRecord(medicalRecord, idBusiness);
            if (objAnswerMedicalR == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Registro médico creado correctamente, Success",
                    "data", objAnswerMedicalR
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el registro médico",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putMedicalRecord/{idMedicalRecord}")
    public ResponseEntity<?> putMedicalRecord(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOMedicalRecord dtoMedicalRecord,
            BindingResult dataResult,
            @PathVariable String idMedicalRecord
        ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoMedicalRecord.setIdBusiness(idBusiness);
            DTOMedicalRecord objAnswerMedicalRecord = objServiceMedicalR.putMedicalRecord(dtoMedicalRecord, idMedicalRecord, idBusiness);
            if (objAnswerMedicalRecord == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Registro médico modificado correctamente, Success",
                    "data", objAnswerMedicalRecord
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (ExceptionDataDuplicate e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "Error", "Datos duplicados, inténtelo otra vez",
                    "Campo duplicado: ", e.getDuplicateData()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el registro médico",
                    "detail", e.getMessage()
            ));
        }
    }
}