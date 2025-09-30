package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryInspectionStatus extends JpaRepository<EntityInspectionStatus, String> {
    @Query("""
            select s.idInspectionStatus
            from EntityInspectionStatus s
            where upper(s.inspectionStatus) = upper(:name)
        """)
    Optional<String> findPendingId(String name);
}
