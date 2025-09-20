package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryUser extends JpaRepository<EntityUser, String> {
    //Native query sirve para ejecutar consultas directamente en la db
    @Query(value = "UPDATE TBUSER SET STATUS = status WHERE USERNAME = username", nativeQuery = true)
    int setStatus(@Param("username") String username,
                  @Param("status")   String status); //'T' o 'F'
}
