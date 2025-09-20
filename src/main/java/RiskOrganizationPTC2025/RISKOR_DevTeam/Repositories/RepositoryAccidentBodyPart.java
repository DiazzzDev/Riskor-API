package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryAccidentBodyPart extends JpaRepository<EntityAccidentBodyPart, String> {
    //Listado por empresa
    List<EntityAccidentBodyPart> findByIdBusiness_IdBusiness(String idBusiness);

    //Borrado más eficiente por empresa
    long deleteByIdAccidentBodyPartAndIdBusiness_IdBusiness(String idAccidentBodyPart, String idBusiness);

    //Verificar existencia dentro de la empresa
    boolean existsByIdAccidentBodyPartAndIdBusiness_IdBusiness(String idAccidentBodyPart, String idBusiness);
}
