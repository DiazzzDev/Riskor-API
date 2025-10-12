package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryLocation extends JpaRepository<EntityLocation, String> {
    //Listado por empresa
    List<EntityLocation> findByIdBusiness_IdBusiness(String idBusiness); //Este método tmb se usa para dashboard

    // Para actualizar/buscar un registro asegurando empresa
    Optional<EntityLocation> findByIdLocationAndIdBusiness_IdBusiness(String idLocation, String idBusiness);

    // Borrado seguro por empresa
    void deleteByIdLocationAndIdBusiness_IdBusiness(String idLocation, String idBusiness);

    boolean existsByIdLocationAndIdBusiness_IdBusiness(String idLocation, String idBusiness);

    List<EntityLocation> findByIdArea_IdAreaAndIdBusiness_IdBusiness(String idArea, String idBusiness);
}