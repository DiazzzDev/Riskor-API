package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentReport;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/accident/report")
public class ControllerAccidentReport {

    @Autowired
    private ServiceAccidentReport reportService;

    @Autowired
    private ServiceAccident objServiceA;

    @PreAuthorize("hasRole('Administrador')")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> descargarReportePdf(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable("id") String id
        ){
        try {
            // Buscar el DTO (ajusta la excepción / manejo si tu servicio lanza  exception)
            DTOAccident dto = objServiceA.getById(id, idBusiness);
            if (dto == null) {
                return ResponseEntity.notFound().build();
            }

            // Generar PDF (tu servicio ya devuelve byte[])
            byte[] pdfBytes = reportService.generatePdfFromDto(dto);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-accidente-" + id + ".pdf\"");
            // Exponer el header para que front pueda leerlo si está en otro dominio
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "No encontrado, Error",
                    "message", "Accidente no encontrado",
                    "timeStamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al consultar el accidente",
                    "detail", e.getMessage()
            ));
        }
    }
}

