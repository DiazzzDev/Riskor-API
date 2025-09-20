package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryEvidence extends JpaRepository<EntityEvidence, String> {
    List<EntityEvidence> findByIdBusiness_IdBusiness(String idBusiness);

    Optional<EntityEvidence> findByIdEvidenceAndIdBusiness_IdBusiness(String idEvidence, String idBusiness);

    long deleteByIdEvidenceAndIdBusiness_IdBusiness(String idEvidence, String idBusiness);
}
