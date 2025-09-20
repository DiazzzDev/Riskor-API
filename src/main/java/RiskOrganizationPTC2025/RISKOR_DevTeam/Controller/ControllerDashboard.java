package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class ControllerDashboard {
    //Llamamos el service
    @Autowired
    private ServiceDashboard objServiceD;

    //Capacitaciones asistidas/Inicio
    @GetMapping("/employee/{idEmployee}/attendance")
    public DTOAttendanceSummary getAttendance(
            @RequestAttribute("auth.business") String idBusiness, @PathVariable String idEmployee){
        return objServiceD.attendance(idBusiness, idEmployee);
    }

    //Calendario
    @GetMapping("/employee/{idEmployee}/calendar")
    public List<DTOCalendarItem> getCalendar(@RequestAttribute("auth.business") String idBusiness, @PathVariable String idEmployee){
        return objServiceD.calendar(idBusiness, idEmployee);
    }

    //Accidentes: este mes vs mes anterior/Admin
    @GetMapping("/accidents/compare")
    public DTOAccidentCompare getAccidentsCompare(@RequestAttribute("auth.business") String idBusiness){
        return objServiceD.accidentsCompare(idBusiness);
    }

    //Gráfico por mes: ?startYM=2025-01&months=6 (min 2, max 6) / Admin
    @GetMapping("/accidents/series")
    public ResponseEntity<?> getAccidentsSeries(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam String startYM,
            @RequestParam(defaultValue = "6") int months) {

        if(months < 2 || months > 6){
            return ResponseEntity.badRequest().body(Map.of(
                    "status","VALIDATION_ERROR","message","months debe estar entre 2 y 6"));
        }
        try{
            List<DTOAccidentMonthCount> data = objServiceD.accidentsSeries(idBusiness, YearMonth.parse(startYM), months);
            return ResponseEntity.ok(data);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of( //Si se manda mal un parámetro va a mandar un 400 bad request
                    "status","VALIDATION_ERROR","message","Parámetro startYM inválido. Formato esperado YYYY-MM"));
        }
    }

    //Áreas de menor y mayor riesgo / Admin
    @GetMapping("/risk-areas/extremes")
    public DTORiskAreasExtremes getRiskAreas(@RequestAttribute("auth.business") String idBusiness){
        return objServiceD.riskAreas(idBusiness);
    }

    //Calificaciones por capacitación / Admin
    @GetMapping("/training/ratings")
    public ResponseEntity<List<DTOTrainingRatings>> getRatings(@RequestAttribute("auth.business") String idBusiness){
        return ResponseEntity.ok(objServiceD.ratingsAllTrainings(idBusiness));
    }
}
