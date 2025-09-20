package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentNotification;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentNotification;
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
@RequestMapping("/api/accidentNotification")
@Validated
public class ControllerAccidentNotification {
    @Autowired
    private ServiceAccidentNotification objServiceAN;

    //GET: ACTIVAS por EMPLEADO - (Método MAIN)
    @GetMapping("/active/employee/{idEmployee}")
    public ResponseEntity<?> listActiveByEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
    ) {
        try {
            List<DTOAccidentNotification> list = objServiceAN.listActiveByEmployee(idEmployee, idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar notificaciones por empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    //GET: por ID
    @GetMapping("/getById/{idAccNotification}")
    public ResponseEntity<?> getById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idAccNotification
    ) {
        try {
            DTOAccidentNotification dto = objServiceAN.getById(idAccNotification, idBusiness);
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

    //GET: ACTIVAS por EMPRESA
    @GetMapping("/active")
    public ResponseEntity<?> listActiveByBusiness(@RequestAttribute("auth.business") String idBusiness) {
        try {
            List<DTOAccidentNotification> list = objServiceAN.listActiveByBusiness(idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar notificaciones",
                    "detail", e.getMessage()
            ));
        }
    }

    //GET: ACTIVAS por ACCIDENTE
    @GetMapping("/active/accident/{idAccident}")
    public ResponseEntity<?> listActiveByAccident(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idAccident
    ) {
        try {
            List<DTOAccidentNotification> list = objServiceAN.listActiveByAccident(idAccident, idBusiness);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al listar notificaciones por accidente",
                    "detail", e.getMessage()
            ));
        }
    }
}