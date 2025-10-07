package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAccident extends JpaRepository<EntityAccident, String>,
                                            JpaSpecificationExecutor<EntityAccident> {
    Page<EntityAccident> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Optional<EntityAccident> findByIdAccidentAndIdBusiness_IdBusiness(String idAccident, String idBusiness);

    long deleteByIdAccidentAndIdBusiness_IdBusiness(String idAccident, String idBusiness);

    //Consultas para dashboard
    long countByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(String idBusiness, LocalDate startInclusive, LocalDate endExclusive);
    List<EntityAccident> findByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(String idBusiness, LocalDate startInclusive, LocalDate endExclusive);
    List<EntityAccident> findByIdBusiness_IdBusiness(String idBusiness);
}
