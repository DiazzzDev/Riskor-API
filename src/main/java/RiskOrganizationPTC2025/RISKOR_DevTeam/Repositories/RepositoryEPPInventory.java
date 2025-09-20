package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepositoryEPPInventory extends JpaRepository<EntityEPPInventory, String> {
    Page<EntityEPPInventory> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);
    Optional<EntityEPPInventory> findByIdEPPInventoryAndIdBusiness_IdBusiness(String idEPPInventory, String idBusiness);

    boolean existsByIdEPPInventoryAndIdBusiness_IdBusiness(String idEPPInventory, String upperCase);

    void deleteByIdEPPInventoryAndIdBusiness_IdBusiness(String idEPPInventory, String upperCase);
}
