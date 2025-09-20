package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAccident extends JpaRepository<EntityAccident, String> {
    Page<EntityAccident> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Optional<EntityAccident> findByIdAccidentAndIdBusiness_IdBusiness(String idAccident, String idBusiness);

    long deleteByIdAccidentAndIdBusiness_IdBusiness(String idAccident, String idBusiness);

    //Consulta definitiva para filtrar los accidentes de la empresa - Es la consulta principal y será modificada a como la interfaz lo requiera
    @Query("""
        SELECT a FROM EntityAccident a
        WHERE a.idBusiness.idBusiness = :idBusiness
          AND (:employeeId IS NULL OR a.idEmployee.idEmployee = :employeeId)
          AND (:statusId   IS NULL OR a.idAccidentStatus.idAccidentStatus = :statusId)
          AND (:fromDate   IS NULL OR a.accidentDate >= :fromDate)
          AND (:toDate     IS NULL OR a.accidentDate <= :toDate)
        """)
    Page<EntityAccident> search(
            @Param("idBusiness") String idBusiness,
            @Param("employeeId") String employeeId,
            @Param("statusId")   String statusId,
            @Param("fromDate")   LocalDate fromDate,
            @Param("toDate")     LocalDate toDate,
            Pageable pageable
    );

    //Consultas para dashboard
    long countByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(String idBusiness, LocalDate startInclusive, LocalDate endExclusive);
    List<EntityAccident> findByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(String idBusiness, LocalDate startInclusive, LocalDate endExclusive);
    List<EntityAccident> findByIdBusiness_IdBusiness(String idBusiness);
}
