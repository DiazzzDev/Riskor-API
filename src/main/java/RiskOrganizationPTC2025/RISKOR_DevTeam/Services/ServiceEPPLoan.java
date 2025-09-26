package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPInventory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanSummary;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEPPLoan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@Transactional
public class ServiceEPPLoan {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryEPPLoan objRepoEPPLoan;

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
    public DTOEPPLoan postEPPLoan(@Valid DTOEPPLoan DTOEPPLoan, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOEPPLoan == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityEPPLoan objeEPPLoanSaved = objRepoEPPLoan.save(convertTOEPPLoanEntity(DTOEPPLoan, idBusiness));

        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOEPPLoanDTO(objeEPPLoanSaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOEPPLoan putEPPLoan(@Valid DTOEPPLoan dtoEppL, String idEPPL, String idBusiness) {
        //Si el registro ingresado fue nulo, retornamos una excepción
        if(dtoEppL == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error (Luego se debe crear excepción personalizada)
        EntityEPPLoan eppLoan = objRepoEPPLoan.findByIdEPPLoanAndIdBusiness_IdBusiness(idEPPL, idBusiness).orElseThrow(() -> new EntityNotFoundException("Equipo EPP no encontrado con ID: " + idEPPL));

        //Añadimos los valores
        eppLoan.setLoanReturnDate(dtoEppL.getLoanReturnDate());
        eppLoan.setQuantityDelivered(dtoEppL.getQuantityDelivered());
        eppLoan.setQuantityReturned(dtoEppL.getQuantityReturned());

        if (dtoEppL.getIdEPPInventory() != null) {
            eppLoan.setIdEPPInventory(em.getReference(EntityEPPInventory.class, dtoEppL.getIdEPPInventory()));
        }

        //Si el valor del empleado ha sido modificado se va a actualizar, sino va a mantener su estado original
        if (dtoEppL.getIdEmployee() != null) {
            eppLoan.setIdEmployee(em.getReference(EntityEmployee.class, dtoEppL.getIdEmployee()));
        }

        return convertTOEPPLoanDTO(eppLoan);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteEPPLoan(String idEPPL, String idBusiness){
        if (!objRepoEPPLoan.existsByIdEPPLoanAndIdBusiness_IdBusiness(idEPPL, idBusiness.toUpperCase())) { return false; }

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
        objEntityEPPLoan.setQuantityReturned(DTOEPPLoan.getQuantityReturned());
        objEntityEPPLoan.setIdEPPInventory(em.getReference(EntityEPPInventory.class, DTOEPPLoan.getIdEPPInventory()));
        objEntityEPPLoan.setIdEmployee(em.getReference(EntityEmployee.class, DTOEPPLoan.getIdEmployee()));
        objEntityEPPLoan.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityEPPLoan;
    }
}
