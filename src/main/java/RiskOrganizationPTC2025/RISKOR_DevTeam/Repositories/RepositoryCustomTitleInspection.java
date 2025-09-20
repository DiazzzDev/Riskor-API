package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityCustomTitleInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryCustomTitleInspection extends JpaRepository<EntityCustomTitleInspection, String> {
    List<EntityCustomTitleInspection> findByIdBusiness_IdBusiness(String upperCase);

    boolean existsByIdCustomTitleInspAndIdBusiness_IdBusiness(String idCustomTitleInsp, String idBusiness);

    void deleteByIdCustomTitleInspAndIdBusiness_IdBusiness(String idCustomTitleInsp, String idBusiness);

    Optional<EntityCustomTitleInspection> findByIdCustomTitleInspAndIdBusiness_IdBusiness(String idCustomTitleInsp, String idBusiness);
}
