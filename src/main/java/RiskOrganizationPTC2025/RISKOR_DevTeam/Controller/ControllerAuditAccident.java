package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAuditAccident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/auditAccident")
@Validated
public class ControllerAuditAccident {
    @Autowired
    private ServiceAuditAccident objServiceAA;

    @GetMapping //Este método puede tener varios filtros en la tabla de auditoría, para que el frontend pueda agregar filtros para mostrar la información con mayor facilidad
    public ResponseEntity<?> getAuditAccident(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(required = false) String operationType,    //INSERT | UPDATE | DELETE
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String accidentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, //Formato de fecha: El que usamos es YYYY-MM-DD
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {
        try {
            if (size < 5 || size > 50) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "El tamaño de la página debe estar entre 5 y 50"
                ));
            }
            if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Validación",
                        "message", "toDate no puede ser menor que fromDate"
                ));
            }
            //Se manda todo el contenido filtrado al cliente con la respectiva paginación
            return ResponseEntity.ok(objServiceAA.search(idBusiness, operationType, username, accidentId, fromDate, toDate, page, size));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar la auditoría de accidentes",
                    "detail", e.getMessage(),
                    "timeStamp", Instant.now().toString()
            ));
        }
    }
}