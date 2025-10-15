package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RepositoryAccidentBodyPart extends JpaRepository<EntityAccidentBodyPart, String> {

    // Listado por empresa
    List<EntityAccidentBodyPart> findByIdBusiness_IdBusiness(String idBusiness);

    // Borrado explícito (devuelve número de filas afectadas)
    @Modifying
    @Transactional
    @Query("DELETE FROM EntityAccidentBodyPart e WHERE e.idAccidentBodyPart = :idAccidentBodyPart AND e.idBusiness.idBusiness = :idBusiness")
    int deleteByIdAccidentBodyPartAndIdBusiness(@Param("idAccidentBodyPart") String idAccidentBodyPart, @Param("idBusiness") String idBusiness);

    // Verificar existencia dentro de la empresa
    boolean existsByIdAccidentBodyPartAndIdBusiness_IdBusiness(String idAccidentBodyPart, String idBusiness);
}