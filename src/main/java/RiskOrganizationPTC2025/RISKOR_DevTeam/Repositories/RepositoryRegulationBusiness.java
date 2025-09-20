package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRegulationBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryRegulationBusiness extends JpaRepository<EntityRegulationBusiness, String> {
    //Paginado por empresa
    Page<EntityRegulationBusiness> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    //Buscar 1 registro asegurando que sea de esa empresa
    Optional<EntityRegulationBusiness> findByIdRegulationAndIdBusiness_IdBusiness(String idRegulation, String idBusiness);

    //Borrar protegido por empresa
    void deleteByIdRegulationAndIdBusiness_IdBusiness(String idRegulation, String idBusiness);
    boolean existsByIdRegulationAndIdBusiness_IdBusiness(String idRegulation, String idBusiness); //Verificar existencia
}
