package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityMaintenanceControlSD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryMaintenanceControlSD extends JpaRepository<EntityMaintenanceControlSD, String> {
    Page<EntityMaintenanceControlSD> findByIdBusiness_IdBusiness(String upperCase, Pageable pageable);

    void deleteByIdMaintenanceControlSDAndIdBusiness_IdBusiness(String idMaintenanceControlSD, String idBusiness);

    boolean existsByIdMaintenanceControlSDAndIdBusiness_IdBusiness(String idMaintenanceControlSD, String idBusiness);

    Optional<EntityMaintenanceControlSD> findByIdMaintenanceControlSDAndIdBusiness_IdBusiness(String idMaintenance, String idBusiness);
}
