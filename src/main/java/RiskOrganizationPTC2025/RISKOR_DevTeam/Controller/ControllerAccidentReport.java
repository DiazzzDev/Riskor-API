package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccident;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAccidentReport;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> descargarReportePdf(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable("id") String id
    ) {
        try {
            DTOAccident dto = objServiceA.getById(id, idBusiness);
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "status", "No encontrado, Error",
                                "message", "Accidente no encontrado",
                                "timeStamp", Instant.now().toString()
                        ));
            }

            byte[] pdfBytes = reportService.generatePdfFromDto(dto);
            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "status", "No Content",
                                "message", "El reporte no contiene datos",
                                "timeStamp", Instant.now().toString()
                        ));
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);
            InputStreamResource resource = new InputStreamResource(bis);

            String fileName = "reporte-accidente-" + id + ".pdf";
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename=\"" + fileName.replace("\"", "") + "\"; filename*=UTF-8''" + encoded;

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            headers.add("Cache-Control", "no-store");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "status", "No encontrado, Error",
                            "message", "Accidente no encontrado",
                            "timeStamp", Instant.now().toString()
                    ));
        } catch (IllegalArgumentException iae) {
            // Captura la excepción de UUID inválido u otros argumentos
            iae.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "status", "Error de plantilla JRXML",
                            "message", iae.getMessage()
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "status", "Error crítico no controlado",
                            "message", "Error al consultar el accidente",
                            "detail", e.getMessage()
                    ));
        }
    }

}