package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccident;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ServiceAccidentReport {
    //JasperReport es una libreria que nos ayuda a realizar los reportes con tipo de archivos jrxml
    private JasperReport compiledReport;

    //JasperReport necesita ser compilado, por lo que con este método obtenemos el archivo .jrxml para después llenarlo con los datos del accidente
    private JasperReport getCompiledReport() throws Exception {
        if (compiledReport != null) return compiledReport;

        //Obtenemos el archivo desde "/resources/reports"
        ClassPathResource jrxmlResource = new ClassPathResource("reports/accident_report.jrxml");
        try (InputStream is = jrxmlResource.getInputStream()) {
            compiledReport = JasperCompileManager.compileReport(is);
            return compiledReport;
        }
    }

    public byte[] generatePdfFromDto(DTOAccident dto) throws Exception {
        Map<String, Object> mapped = mapDtoToFields(dto);
        List<Map<String, Object>> dataList = Collections.singletonList(mapped);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

        // parámetros vacíos (no quieres COMPANY_NAME)
        Map<String, Object> parameters = new HashMap<>();

        // Llenar reporte: jasperReport, parameters, datasource
        JasperPrint jasperPrint = JasperFillManager.fillReport(getCompiledReport(), parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }


    //Método para mapear a DTO los campos dentro de la plantilla jrxml para luego exportala a PDF
    private Map<String, Object> mapDtoToFields(DTOAccident dto) {
        Map<String, Object> m = new HashMap<>();
        m.put("idAccident", dto.getIdAccident());
        m.put("description", dto.getDescription());
        m.put("accidentDate", toDate(dto.getAccidentDate()));
        m.put("reportAccident", toDate(dto.getReportAccident()));
        m.put("idAccidentCategory", dto.getIdAccidentCategory());
        m.put("idAccidentType", dto.getIdAccidentType());
        m.put("idAccidentSeverity", dto.getIdAccidentSeverity());
        m.put("idAccidentStatus", dto.getIdAccidentStatus());
        m.put("idEmployee", dto.getIdEmployee());
        m.put("idLocation", dto.getIdLocation());
        m.put("idBusiness", dto.getIdBusiness());
        m.put("sentBy", dto.getSentBy());
        return m;
    }

    //Formato de fecha preferencial para el PDF
    private Date toDate(LocalDate ld) {
        if (ld == null) return null;
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
