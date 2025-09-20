package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspectionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryInspectionItem extends JpaRepository<EntityInspectionItem, String> {
    void deleteByIdInspectionItemAndIdBusiness_IdBusiness(String idInspectionItem, String idBusiness);

    boolean existsByIdInspectionItemAndIdBusiness_IdBusiness(String idInspectionItem, String idBusiness);

    Page<EntityInspectionItem> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Optional<EntityInspectionItem> findByIdInspectionItemAndIdBusiness_IdBusiness(String idInspectionItem, String idBusiness);
}
