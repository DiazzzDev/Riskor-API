package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOQuestionEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceQuestionEmployee;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/apiQuestionE")
@Validated
public class ControllerQuestionEmployee {
    //Inyectamos el Service
    @Autowired
    private ServiceQuestionEmployee objServiceQuestionE;

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getQuestionE")
    public List<DTOQuestionEmployee> getData(){
        return objServiceQuestionE.getAllQuestionE();
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postQuestionE")
//Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postData(@Valid @RequestBody DTOQuestionEmployee questionEmployee) {
        try {
            //Indicamos los valores del DTO indicaran la respuesta dirigiendose al Service, recibiendo como parámetros los valores de los campos
            DTOQuestionEmployee objAnswerQuestionE = objServiceQuestionE.postQuestionE(questionEmployee);
            if (objAnswerQuestionE == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Respuesta de pregunta de seguridad registrada correctamente, Success",
                    "data", objAnswerQuestionE
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar la respuesta de pregunta de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putQuestionEmployee/{idQuestionsEmployee}")
    public ResponseEntity<?> putData(@Valid @RequestBody DTOQuestionEmployee dtoQuestionEmployee, @PathVariable String idQuestionsEmployee, BindingResult dataResult) {

        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DTOQuestionEmployee objAnswerQuestionEmployee = objServiceQuestionE.putQuestionEmployee(dtoQuestionEmployee, idQuestionsEmployee);
            if (objAnswerQuestionEmployee == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Respuesta de pregunta de seguridad modificada correctamente, Success",
                    "data", objAnswerQuestionEmployee
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar la respuesta de pregunta de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteQuestionEmployee/{idQuestionsEmployee}")
    public ResponseEntity<?> deleteData(@Valid @PathVariable String idQuestionsEmployee) {
        try {
            if (!objServiceQuestionE.deleteQuestionE(idQuestionsEmployee)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID de la Pregunta de Seguridad al Empleado no encontrada").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID de la pregunta de seguridad no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Pregunta de seguridad eliminada correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar la pregunta de seguridad",
                    "detail", e.getMessage()
            ));
        }
    }
}
