package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingNotification;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceTrainingNotification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainingNotification")
@Validated
public class ControllerTrainingNotification {
    @Autowired
    private ServiceTrainingNotification objServiceTN;

    //GET por ID
    @GetMapping("/getById/{idTrnNotification}")
    public ResponseEntity<?> getById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTrnNotification) {
        try {
            DTOTrainingNotification dto = objServiceTN.getById(idTrnNotification, idBusiness);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Notificación no encontrada",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar la notificación",
                    "detail", e.getMessage()
            ));
        }
    }

    //GET activas por EMPRESA
    @GetMapping("/active")
    public ResponseEntity<?> listActiveByBusiness(@RequestAttribute("auth.business") String idBusiness) {
        try {
            List<DTOTrainingNotification> list = objServiceTN.listActiveByBusiness(idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar notificaciones",
                    "detail", e.getMessage()
            ));
        }
    }

    //GET activas por EMPLEADO
    @GetMapping("/active/employee/{idEmployee}")
    public ResponseEntity<?> listActiveByEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee) {
        try {
            List<DTOTrainingNotification> list = objServiceTN.listActiveByEmployee(idEmployee, idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar notificaciones por empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    //GET activas por CAPACITACIÓN
    @GetMapping("/active/training/{idTraining}")
    public ResponseEntity<?> listActiveByTraining(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idTraining) {
        try {
            List<DTOTrainingNotification> list = objServiceTN.listActiveByTraining(idTraining, idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar notificaciones por capacitación",
                    "detail", e.getMessage()
            ));
        }
    }
}
