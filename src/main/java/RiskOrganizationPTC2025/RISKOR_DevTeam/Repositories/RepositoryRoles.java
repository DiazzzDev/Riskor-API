package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryRoles extends JpaRepository<EntityRoles, String> {
    @Query("""
           select r.idRole
           from EntityRoles r
           where upper(r.roleName) = 'ADMINISTRADOR'
           """)
    Optional<String> findAdministratorId();
}
