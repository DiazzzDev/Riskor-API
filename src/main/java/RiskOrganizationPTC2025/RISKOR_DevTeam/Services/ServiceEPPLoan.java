package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPInventory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanSummary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEPPInventory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec.EPPLoanSpecs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@Transactional
public class ServiceEPPLoan {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryEPPLoan objRepoEPPLoan;

    //Inyectamos repo de inventario para modificar los epp disponibles
    @Autowired
    private RepositoryEPPInventory objRepoEPPInventory;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    @Transactional(readOnly = true)
    public DTOEPPLoan getEPPLoanById(String idBusiness, String idEPPLoan) {
        EntityEPPLoan entityEPPLoan = objRepoEPPLoan.findByIdEPPLoanAndIdBusiness_IdBusiness(idEPPLoan, idBusiness).orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado con ID: " + idEPPLoan));
        return convertTOEPPLoanDTO(entityEPPLoan);
    }

    @Transactional(readOnly = true)
    public DTOEPPLoanSummary getLoanSummaryByEmployee(String idBusiness, String idEmployee) {
        return objRepoEPPLoan.getLoanSummaryByEmployee(idBusiness.toUpperCase(), idEmployee.toUpperCase());
    }

    @Transactional(readOnly = true)
    public Page<DTOEPPLoan> searchByEmployeeName(
            String idBusiness, String employeeName,
            LocalDate startDate, LocalDate endDate,
            int page, int size
    ){
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.asc("idEmployee.lastName"),
                        Sort.Order.asc("idEmployee.firstName"),
                        Sort.Order.desc("loanStartDate")
                )
        );

        Specification<EntityEPPLoan> spec = EPPLoanSpecs.scope(idBusiness)
                .and(EPPLoanSpecs.byEmployeeName(employeeName))
                .and(EPPLoanSpecs.inDateRange(startDate, endDate));

        Page<EntityEPPLoan> loans = objRepoEPPLoan.findAll(spec, pageable);
        return loans.map(this::convertTOEPPLoanDTO);
    }

    @Transactional(readOnly = true)
    public Page<DTOEPPLoan> getAllEPPLoansByEmployee(String idBusiness, String idEmployee, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<EntityEPPLoan> loans;

        //Acá si se ingresaron las fechas va a realizarse la consulta JPA que corresponde
        if (startDate != null && endDate != null) {
            loans = objRepoEPPLoan.findByIdBusiness_IdBusinessAndIdEmployee_IdEmployeeAndLoanStartDateBetween(
                    idBusiness.toUpperCase(),
                    idEmployee.toUpperCase(),
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            //Si no se proporcionan fechas, retorna todos los préstamos del empleado
            loans = objRepoEPPLoan.findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(
                    idBusiness.toUpperCase(),
                    idEmployee.toUpperCase(),
                    pageable
            );
        }

        return loans.map(this::convertTOEPPLoanDTO);
    }

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOEPPLoan> getAllEPPLoan(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEPPLoan> loans = objRepoEPPLoan.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return loans.map(this::convertTOEPPLoanDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOEPPLoan postEPPLoan(@Valid DTOEPPLoan dtoEppLoan, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (dtoEppLoan == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        int delivered = dtoEppLoan.getQuantityDelivered();

        int updated = objRepoEPPInventory.decrementAvailable(idBusiness.toUpperCase(), dtoEppLoan.getIdEPPInventory(), delivered);

        //Si no hubo suficiente stock UPDATED lanzará 0, significa que no pudo descontar la cantidad del inventario
        if (updated == 0) throw new IllegalArgumentException("Inventario insuficiente."); //Lanzando excepción por falta de stock

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityEPPLoan saved = objRepoEPPLoan.save(convertTOEPPLoanEntity(dtoEppLoan, idBusiness));
        return convertTOEPPLoanDTO(saved); //Convertimos a DTO/JSON para mostrar al frontend
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOEPPLoan putEPPLoan(@Valid DTOEPPLoan dtoEppL, String idEPPL, String idBusiness) {
        //Si el registro ingresado fue nulo, retornamos una excepción
        if(dtoEppL == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Verifica si existe el Registro que se va a actualizar, si no existe lanza error
        EntityEPPLoan loan = objRepoEPPLoan.findByIdEPPLoanAndIdBusiness_IdBusiness(idEPPL, idBusiness).orElseThrow(() -> new EntityNotFoundException("Equipo EPP no encontrado con ID: " + idEPPL));

        int oldDelivered = loan.getQuantityDelivered();
        int oldReturned  = loan.getQuantityReturned();
        String oldInvId  = loan.getIdEPPInventory().getIdEPPInventory();

        int newDelivered = dtoEppL.getQuantityDelivered();
        int newReturned  = dtoEppL.getQuantityReturned();
        validateLoanNumbers(newDelivered, newReturned);

        String newInvId  = (dtoEppL.getIdEPPInventory() != null) ? dtoEppL.getIdEPPInventory() : oldInvId;

        //Evitamos actualizar si el PAYLOAD del JSON es el mismo, optimización y rendimiento
        boolean sameDate = java.util.Objects.equals(loan.getLoanReturnDate(), dtoEppL.getLoanReturnDate());
        if (oldInvId.equals(newInvId)
                && oldDelivered == newDelivered
                && oldReturned == newReturned
                && sameDate) {
            return convertTOEPPLoanDTO(loan);
        }

        int oldPending = oldDelivered - oldReturned;
        int newPending = newDelivered - newReturned;

        if (oldInvId.equals(newInvId)) {
            //MISMO INVENTARIO: ajustar por diferencia de pendientes
            int deltaInv = oldPending - newPending; // + => regresa stock, - => toma stock

            if (deltaInv > 0) {
                int rows = objRepoEPPInventory.incrementAvailable(idBusiness.toUpperCase(), oldInvId, deltaInv);
                if (rows == 0) throw new IllegalArgumentException("Devolución inválida: excede el total del inventario.");
            } else if (deltaInv < 0) {
                int rows = objRepoEPPInventory.decrementAvailable(idBusiness.toUpperCase(), oldInvId, -deltaInv);
                if (rows == 0) throw new IllegalArgumentException("Inventario insuficiente para aumentar el pendiente del préstamo.");
            }
        } else {
            //CAMBIO DE INVENTARIO → revertir pendientes al viejo y aplicar pendientes al nuevo
            if (oldPending > 0) {
                int rows = objRepoEPPInventory.incrementAvailable(idBusiness.toUpperCase(), oldInvId, oldPending);
                if (rows == 0) throw new IllegalStateException("Inconsistencia de stock al revertir en inventario anterior.");
            }
            if (newPending > 0) {
                int rows = objRepoEPPInventory.decrementAvailable(idBusiness.toUpperCase(), newInvId, newPending);
                if (rows == 0) throw new IllegalArgumentException("Inventario insuficiente en el nuevo EPP.");
            }
            //Actualiza referencia SOLO si cambió
            loan.setIdEPPInventory(em.getReference(EntityEPPInventory.class, newInvId));
        }

        //Actualizar préstamo
        loan.setLoanReturnDate(dtoEppL.getLoanReturnDate());
        loan.setQuantityDelivered(newDelivered);
        loan.setQuantityReturned(newReturned);

        //No vamos a permitir que se modifique el empleado que realizó el cambio
        return convertTOEPPLoanDTO(loan); //Convertimos a DTO/JSON para mandarlo al frontend
    }

    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteEPPLoan(String idEPPL, String idBusiness){
        //Este método devolverá a STOCK los equipos prestados al inventario
        //Buscará el prestamo por ID, si no lo encuentra null
        EntityEPPLoan loan = objRepoEPPLoan.findByIdEPPLoanAndIdBusiness_IdBusiness(idEPPL, idBusiness.toUpperCase()).orElse(null);
        if (loan == null) return false; //Si es nulo no lo puede eliminar porque no existe o no lo encontró

        int pending = loan.getQuantityDelivered() - loan.getQuantityReturned();
        //Si el pendiente es mayor a 0 devolverá a la tabla INVENTARIO su cantidad correspondiente con una consulta personalizada de la JPA
        if (pending > 0) objRepoEPPInventory.incrementAvailable(idBusiness.toUpperCase(), loan.getIdEPPInventory().getIdEPPInventory(), pending);

        //Finalmente eliminará el registro del préstamo por su ID y por el ID de la empresa para evitar eliminar datos de otra empresa
        objRepoEPPLoan.deleteByIdEPPLoanAndIdBusiness_IdBusiness(idEPPL, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOEPPLoan convertTOEPPLoanDTO(EntityEPPLoan eppLoan){
        DTOEPPLoan objEPPLoanDTO = new DTOEPPLoan();
        objEPPLoanDTO.setIdEPPLoan(eppLoan.getIdEPPLoan());
        objEPPLoanDTO.setLoanStartDate(eppLoan.getLoanStartDate());
        objEPPLoanDTO.setLoanReturnDate(eppLoan.getLoanReturnDate());
        objEPPLoanDTO.setQuantityDelivered(eppLoan.getQuantityDelivered());
        objEPPLoanDTO.setQuantityReturned(eppLoan.getQuantityReturned());
        objEPPLoanDTO.setIdEPPInventory(eppLoan.getIdEPPInventory().getIdEPPInventory());
        objEPPLoanDTO.setNameEPP(eppLoan.getIdEPPInventory().getNameEPP());
        objEPPLoanDTO.setIdEmployee(eppLoan.getIdEmployee().getIdEmployee());
        //Si el objeto idBusiness existe en la entidad area, obtén su ID; si no, simplemente asigna null - Esto por el uso de FETCH LAZY
        objEPPLoanDTO.setIdBusiness(eppLoan.getIdBusiness() != null ? eppLoan.getIdBusiness().getIdBusiness() : null);

        return objEPPLoanDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityEPPLoan convertTOEPPLoanEntity(DTOEPPLoan DTOEPPLoan, String idBusiness){
        EntityEPPLoan objEntityEPPLoan = new EntityEPPLoan();
        objEntityEPPLoan.setLoanStartDate(LocalDate.now()); //Asignamos que se crea desde hoy la fecha de inicio del prestamo
        objEntityEPPLoan.setLoanReturnDate(DTOEPPLoan.getLoanReturnDate());
        objEntityEPPLoan.setQuantityDelivered(DTOEPPLoan.getQuantityDelivered());
        objEntityEPPLoan.setQuantityReturned(0); //No vamos a tomar del JSON en el POST, por defecto no se ha retornado nada de lo prestado
        objEntityEPPLoan.setIdEPPInventory(em.getReference(EntityEPPInventory.class, DTOEPPLoan.getIdEPPInventory()));
        objEntityEPPLoan.setIdEmployee(em.getReference(EntityEmployee.class, DTOEPPLoan.getIdEmployee()));
        objEntityEPPLoan.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityEPPLoan;
    }

    //Validación para evitar valores que sobrepasen
    private void validateLoanNumbers(int delivered, int returned) {
        if (returned > delivered) throw new IllegalArgumentException("La cantidad devuelta no puede ser mayor que la entregada.");
    }
}
