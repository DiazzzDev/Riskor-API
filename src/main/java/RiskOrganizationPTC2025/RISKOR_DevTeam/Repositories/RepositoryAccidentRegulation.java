package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentRegulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryAccidentRegulation extends JpaRepository<EntityAccidentRegulation, String> {
    List<EntityAccidentRegulation> findByIdBusiness_IdBusiness(String idBusiness);
}
