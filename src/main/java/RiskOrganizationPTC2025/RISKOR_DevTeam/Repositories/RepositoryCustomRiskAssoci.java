package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityCustomRiskAssoci;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryCustomRiskAssoci extends JpaRepository<EntityCustomRiskAssoci, String> {
    List<EntityCustomRiskAssoci> findByIdBusiness_IdBusiness(String idBusiness);

    void deleteByIdCustomRiskAssociAndIdBusiness_IdBusiness(String idCustomTitleInsp, String idBusiness);

    boolean existsByIdCustomRiskAssociAndIdBusiness_IdBusiness(String idCustomRiskAssoci, String idBusiness);

    Optional<EntityCustomRiskAssoci> findByIdCustomRiskAssociAndIdBusiness_IdBusiness(String idCustomRiskAssoci, String idBusiness);

    List<EntityCustomRiskAssoci> findByIdBusiness_IdBusinessAndIdCustomTitleInsp_IdCustomTitleInsp(String idBusiness, String idCustomTitleInsp);
}
