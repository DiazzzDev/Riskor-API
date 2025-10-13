package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepositoryInspection extends JpaRepository<EntityInspection, String> {
    void deleteByIdInspectionAndIdBusiness_IdBusiness(String idInspection, String idBusiness);

    boolean existsByIdInspectionAndIdBusiness_IdBusiness(String idInspection, String idBusiness);

    Optional<EntityInspection> findByIdInspectionAndIdBusiness_IdBusiness(String idInspection, String idBusiness);

    Page<EntityInspection> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Page<EntityInspection> findByInspectionTitleContainingIgnoreCaseAndIdBusiness_IdBusiness(String trim, String upperCase, Pageable pageable);
}
