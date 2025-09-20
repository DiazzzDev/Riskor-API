package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityControlSDSSO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryControlSDSSO extends JpaRepository<EntityControlSDSSO, String> {
    Page<EntityControlSDSSO> findAll(Pageable pageable);

    boolean existsByIdServiceDeviceSSOAndIdBusiness_IdBusiness(String idServiceDeviceSSO, String upperCase);

    void deleteByIdServiceDeviceSSOAndIdBusiness_IdBusiness(String idServiceDeviceSSO, String upperCase);

    Optional<EntityControlSDSSO> findByIdServiceDeviceSSOAndIdBusiness_IdBusiness(String idControlSDSSO, String idBusiness);

    Page<EntityControlSDSSO> findByIdBusiness_IdBusiness(Pageable pageable, String idBusiness);
}
