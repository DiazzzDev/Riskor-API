package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPLoan;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RepositoryEPPLoan extends JpaRepository<EntityEPPLoan, String> {
    //Obtener todos por empresa y con paginación
    Page<EntityEPPLoan> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Optional<EntityEPPLoan> findByIdEPPLoanAndIdBusiness_IdBusiness(String idEPPL, String idBusiness);

    boolean existsByIdEPPLoanAndIdBusiness_IdBusiness(String idArea, String idBusiness);
    void deleteByIdEPPLoanAndIdBusiness_IdBusiness(String idArea, String idBusiness);

    //Método para obtener todos los préstamos de un empleado dentro de una empresa
    Page<EntityEPPLoan> findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(
            String idBusiness,
            String idEmployee,
            Pageable pageable
    );

    //Método que se encarga de mostrar todos los prestamos EPP por empleado entre fechas
    Page<EntityEPPLoan> findByIdBusiness_IdBusinessAndIdEmployee_IdEmployeeAndLoanStartDateBetween(
            String idBusiness,
            String idEmployee,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    //Sentencia de la Java Persistence Query Language para CALCULAR los EPP entregados y devueltos para un empleado específico
    //Buscamos el DTO personalizado para cargar los datos necesarios y no cargar la entidad completa (Eficiencia)
    /**
     * COALESCE(, 0)                Sirve para devolver 0 en lugar de null en caso no encuentre los registros para sumarlos
     * SUM(e.quantityDelivered)     Calcula el total de la columna seleccionada
     */
    @Query("SELECT new RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanSummary(" +
            "COALESCE(SUM(e.quantityDelivered),0), COALESCE(SUM(e.quantityReturned),0)) " +
            "FROM EntityEPPLoan e " +
            "WHERE e.idBusiness.idBusiness = :idBusiness " +
            "AND e.idEmployee.idEmployee = :idEmployee")
    DTOEPPLoanSummary getLoanSummaryByEmployee(
            @Param("idBusiness") String idBusiness,
            @Param("idEmployee") String idEmployee
    );
}
