package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryBusinessInfo extends JpaRepository<EntityBusinessInfo, String> {
    Optional<EntityBusinessInfo> findById(String idBusiness);
    boolean existsByNameBusinessIgnoreCase(String nameBusiness);
    boolean existsByEmailBusinessIgnoreCase(String emailBusiness);
}
