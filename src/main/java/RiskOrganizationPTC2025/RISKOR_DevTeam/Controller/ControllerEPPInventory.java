package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPInventory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceEPPInventory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/EPPInventory")
@Validated
@PreAuthorize("hasAnyRole('Administrador', 'Mantenimiento')")
public class ControllerEPPInventory {
    //Inyectamos el Service
    @Autowired
    private ServiceEPPInventory objServiceEPPInventory;

    @GetMapping("/getEPPInventory/{idEPPInventory}")
    public ResponseEntity<?> getById(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEPPInventory
    ){
        return ResponseEntity.ok(objServiceEPPInventory.getEPPInventoryById(idBusiness, idEPPInventory));
    }

    @GetMapping("/getAllEPPInventory")
    public ResponseEntity<?> getAllData(
            @RequestAttribute("auth.business") String idBusiness
    ){
        return ResponseEntity.ok(objServiceEPPInventory.getAllEPPInventory(idBusiness));
    }

    //GetMapping para indicar la URL de nuestra API, GET
    @GetMapping("/getEPPInventory")
    public ResponseEntity<?> getData(
            @RequestAttribute("auth.business") String idBusiness,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        if(size <= 0 || size > 30) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 30"
            ));
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(objServiceEPPInventory.getAllEPPInventory(idBusiness, page, size));
    }

    //Creación del método POST (HTTP Request API), utilización de PostMapping
    @PostMapping("/postEPPInventory")
    //Utilización de ResponseEntity para indicar los parámetros enviados a la Entidad (DB)
    public ResponseEntity<?> postData(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPInventory eppInventory
    ) {
        try {
            eppInventory.setIdBusiness(idBusiness);
            //Indicamos los valores del DTO indicarán la respuesta dirigiéndose al Service, recibiendo como parámetros los valores de los campos
            DTOEPPInventory objAnswerEPPI = objServiceEPPInventory.postEPPInventory(eppInventory, idBusiness);
            if (objAnswerEPPI == null) {
                //Si la respuesta fue nula, se arrojará una badRequest con datos inválidos
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al guardar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Inventario del Equipo de Protección Personal registrado correctamente, Success",
                    "data", objAnswerEPPI
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al registrar el inventario del EPP",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/putEPPInventory/{idEPPInventory}")
    public ResponseEntity<?> putData(
            @RequestAttribute("auth.business") String idBusiness,
            @Valid @RequestBody DTOEPPInventory dtoEPPInventory,
            @PathVariable String idEPPInventory,
            BindingResult dataResult)
    {

        //Validamos si existen errores ANTES de proceder con el PUT dentro de los datos solicitados (método de seguridad)
        if (dataResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            dataResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            dtoEPPInventory.setIdBusiness(idBusiness);
            DTOEPPInventory objAnswerEPPInventory = objServiceEPPInventory.putEPPInventory(dtoEPPInventory, idEPPInventory, idBusiness);
            if (objAnswerEPPInventory == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Error al actualizar los datos",
                        "errorType", "VALIDATION_ERROR",
                        "message", "Datos inválidos, vuelva a intentarlo"
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", "Inventario de elemento del inventario modificado correctamente, Success",
                    "data", objAnswerEPPInventory
            ));
        } catch (ExceptionDataNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al actualizar el inventario del EPP",
                    "detail", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteEPPInventory/{idEPPInventory}")
    public ResponseEntity<?> deleteEPPInventory(
            @RequestAttribute("auth.business") String idBusiness,
            @PathVariable String idEPPInventory
    ){
        try {
            boolean ok = objServiceEPPInventory.deleteEPPInventory(idEPPInventory, idBusiness);
            if (!ok){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                        "Error, ID no encontrado", "ID del Prestamo EPP no encontrado").body(Map.of(
                        "status", "No encontrado, Error",
                        "message", "El ID del equipo de protección personal del inventario EPP no ha sido encontrado",
                        "timeStamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "Proceso completado correctamente",
                    "message", "Equipo de protección personal eliminado de inventario correctamente, Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error crítico no controlado",
                    "message", "Error al eliminar el equipo de protección personal",
                    "detail", e.getMessage()
            ));
        }
    }
}