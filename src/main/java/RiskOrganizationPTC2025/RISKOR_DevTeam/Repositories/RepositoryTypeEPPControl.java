package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypeEPPControl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryTypeEPPControl extends JpaRepository<EntityTypeEPPControl, String> {
    boolean existsByIdTypeEPPControlAndIdBusiness_IdBusiness(String idTypeEPPControl, String idBusiness);

    void deleteByIdTypeEPPControlAndIdBusiness_IdBusiness(String idTypeEPPControl, String idBusiness);

    Optional<EntityTypeEPPControl> findByIdTypeEPPControlAndIdBusiness_IdBusiness(String idTypeEPPControl, String idBusiness);

    Page<EntityTypeEPPControl> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    List<EntityTypeEPPControl> findByIdBusiness_IdBusiness(String idBusiness);
}
