package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanSummary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEPPLoan;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/EPPLoan")
@Validated
@PreAuthorize("hasAnyRole('Administrador', 'Mantenimiento')")
public class ControllerEPPLoan {
    //Inyectamos el Service
    @Autowired
    private ServiceEPPLoan objServiceEPPLoan;

    @GetMapping("/getEPPLoan/{idEPPLoan}")
    public ResponseEntity<?> getById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEPPLoan
    ){
        return ResponseEntity.ok(objServiceEPPLoan.getEPPLoanById(idBusiness, idEPPLoan));
    }

    @GetMapping("/summary/{idEmployee}")
    public ResponseEntity<DTOEPPLoanSummary> getEPPLoanSummary(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee
    ) {
        DTOEPPLoanSummary summary = objServiceEPPLoan.getLoanSummaryByEmployee(idBusiness, idEmployee);
        return ResponseEntity.ok(summary);
    }

    //Endpoint para el mostrar todos los préstamos de un empleado
    @GetMapping("/getAllEPPLoansByEmployee/{idEmployee}")
    public ResponseEntity<Page<DTOEPPLoan>> getLoanByEmployee(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEmployee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate endDate
    ){
        if(size <= 0 || size > 30){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(objServiceEPPLoan.getAllEPPLoansByEmployee(idBusiness, idEmployee, startDate, endDate, page, size));
    }

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getEPPLoan")
    public ResponseEntity<Page<DTOEPPLoan>> getLoan(
            @RequestAttribute("auth.business") String idBusiness, //Se coloca PathVariable por semántica y evitar problemas de mezcla de datos con el cliente-empresa
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ){
        if(size <= 0 || size > 30){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(objServiceEPPLoan.getAllEPPLoan(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postEPPLoan")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postLoan(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPLoan dtoEPPLoan
    ){
        try {
            dtoEPPLoan.setIdBusiness(idBusiness);
            DTOEPPLoan out = objServiceEPPLoan.postEPPLoan(dtoEPPLoan, idBusiness);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Préstamo registrado correctamente, Success",
                    "data", out
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "Error de validación",
                    "message", e.getMessage()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "Recurso no encontrado",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el préstamo",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putEPPLoan/{idEPPLoan}")
    public ResponseEntity<?> putLoan(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPLoan dtoEPPLoan,
            BindingResult dataResult,
            @PathVariable String idEPPLoan
    ){
        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            dtoEPPLoan.setIdBusiness(idBusiness);
            DTOEPPLoan updated = objServiceEPPLoan.putEPPLoan(dtoEPPLoan, idEPPLoan, idBusiness);
            if (updated == null) {
                //Por si tu service llegara a devolver null (no debería con @Transactional)
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Préstamo de equipo de protección personal modificado correctamente, Success",
                    "data", updated
            ));
        } catch (IllegalArgumentException e) {
            //Reglas: returned > delivered, inventario insuficiente, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "Error de validación",
                    "message", e.getMessage()
            ));

        } catch (EntityNotFoundException e) {
            //IDs inexistentes (préstamo/EPP/empleado/negocio)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "Recurso no encontrado",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            //Cualquier otra excepción: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el préstamo",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteEPPLoan/{idEPPLoan}")
    public ResponseEntity<?> deleteLoan(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @PathVariable String idEPPLoan
        ){
        try {
            boolean ok = objServiceEPPLoan.deleteEPPLoan(idEPPLoan, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del Prestamo EPP no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del Préstamo EPP no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Préstamo de equipo de protección personal eliminado correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el préstamo",
                    "detail", e.getMessage()
            ));
        }
    }
}
