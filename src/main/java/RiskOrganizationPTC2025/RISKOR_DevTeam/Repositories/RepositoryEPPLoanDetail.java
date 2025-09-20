package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPLoanDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryEPPLoanDetail extends JpaRepository<EntityEPPLoanDetail, String> {
    Page<EntityEPPLoanDetail> findByIdBusiness_IdBusiness(String upperCase, Pageable pageable);

    boolean existsByIdEPPLoanDetailAndIdBusiness_IdBusiness(String idEPPLoanDetail, String upperCase);

    void deleteByIdEPPLoanDetailAndIdBusiness_IdBusiness(String idEPPLoanDetail, String upperCase);

    Optional<EntityEPPLoanDetail> findByIdEPPLoanDetailAndIdBusiness_IdBusiness(String ideppLD, String idBusiness);
}
