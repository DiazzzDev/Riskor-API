package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypeControlSafetyDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryTypeControlSafetyDevice extends JpaRepository<EntityTypeControlSafetyDevice, String> {
    void deleteByIdTypeControlSDAndIdBusiness_IdBusiness(String idTypeControlSD, String upperCase);

    boolean existsByIdTypeControlSDAndIdBusiness_IdBusiness(String idTypeControlSD, String upperCase);

    Page<EntityTypeControlSafetyDevice> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    Optional<EntityTypeControlSafetyDevice> findByIdTypeControlSDAndIdBusiness_IdBusiness(String idTypeEPPControl, String idBusiness);
}
