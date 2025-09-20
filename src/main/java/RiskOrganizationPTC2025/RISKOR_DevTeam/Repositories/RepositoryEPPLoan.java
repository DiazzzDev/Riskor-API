package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPLoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryEPPLoan extends JpaRepository<EntityEPPLoan, String> {
    //Obtener todos por empresa y con paginación
    Page<EntityEPPLoan> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Optional<EntityEPPLoan> findByIdEPPLoanAndIdBusiness_IdBusiness(String idEPPL, String idBusiness);

    boolean existsByIdEPPLoanAndIdBusiness_IdBusiness(String idArea, String idBusiness);
    void deleteByIdEPPLoanAndIdBusiness_IdBusiness(String idArea, String idBusiness);
}
