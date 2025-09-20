package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAuditRegulationBusiness;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRegulationBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryAuditRegulationBusiness extends JpaRepository<EntityAuditRegulationBusiness, String> {
    List<EntityAuditRegulationBusiness> findByIdBusiness(String idBusiness);
}
