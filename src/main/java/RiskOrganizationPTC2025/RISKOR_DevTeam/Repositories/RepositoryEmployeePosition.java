package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployeePosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryEmployeePosition extends JpaRepository<EntityEmployeePosition, String> {
    List<EntityEmployeePosition> findAllByIdBusiness_IdBusiness(String idBusiness);
    Optional<EntityEmployeePosition> findByIdEmployeePositionAndIdBusiness_IdBusiness(String id, String idBusiness);

    void deleteByIdEmployeePositionAndIdBusiness_IdBusiness(String idEmployeePosition, String idBusiness);
    boolean existsByIdEmployeePositionAndIdBusiness_IdBusiness(String idEmployeePosition, String idBusiness);

    Page<EntityEmployeePosition> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);
}
