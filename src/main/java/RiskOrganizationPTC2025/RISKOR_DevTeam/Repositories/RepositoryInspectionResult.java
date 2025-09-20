package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspectionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryInspectionResult extends JpaRepository<EntityInspectionResult, String> {
    Page<EntityInspectionResult> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    void deleteByIdInspectionResultAndIdBusiness_IdBusiness(String idInspectionResult, String idBusiness);

    boolean existsByIdInspectionResultAndIdBusiness_IdBusiness(String idInspectionResult, String idBusiness);

    Optional<EntityInspectionResult> findByIdInspectionResultAndIdBusiness_IdBusiness(String idInspectionResult, String idBusiness);
}
