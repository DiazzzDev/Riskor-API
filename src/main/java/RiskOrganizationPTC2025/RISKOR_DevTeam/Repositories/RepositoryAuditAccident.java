package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAuditAccident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RepositoryAuditAccident extends JpaRepository<EntityAuditAccident, String> {
    Page<EntityAuditAccident> findByIdBusiness(String idBusiness, Pageable pageable);

    //Auditoría 100% personalizada para filtros en el consumo de la API
    @Query("""
        SELECT a FROM EntityAuditAccident a
        WHERE a.idBusiness = :idBusiness
          AND (:operationType IS NULL OR a.operationType = :operationType)
          AND (:username     IS NULL OR UPPER(a.username) LIKE UPPER(CONCAT('%', :username, '%')))
          AND (:accidentId   IS NULL OR a.idAccident = :accidentId)
          AND (:fromDate     IS NULL OR a.operationDate >= :fromDate)
          AND (:toDate       IS NULL OR a.operationDate <= :toDate)
        """)
    Page<EntityAuditAccident> search(
            @Param("idBusiness") String idBusiness,
            @Param("operationType") String operationType,
            @Param("username") String username,
            @Param("accidentId") String accidentId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);
}
