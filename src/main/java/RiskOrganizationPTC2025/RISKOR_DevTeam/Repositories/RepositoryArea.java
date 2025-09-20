package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryArea extends JpaRepository<EntityArea, String> {
    // Listado paginado por empresa
    Page<EntityArea> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    // Buscar un área por ID dentro de una empresa
    Optional<EntityArea> findByIdAreaAndIdBusiness_IdBusiness(String idArea, String idBusiness);

    // Existencia y borrado seguros por empresa
    boolean existsByIdAreaAndIdBusiness_IdBusiness(String idArea, String idBusiness);
    void deleteByIdAreaAndIdBusiness_IdBusiness(String idArea, String idBusiness);
}
