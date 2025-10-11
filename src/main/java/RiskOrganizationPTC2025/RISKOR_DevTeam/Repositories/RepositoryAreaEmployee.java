package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAreaEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAreaEmployee extends JpaRepository<EntityAreaEmployee, String> {
    List<EntityAreaEmployee> findByIdBusiness_IdBusiness(String idBusiness);

    // Para aislar por empresa al leer/editar/borrar
    Optional<EntityAreaEmployee> findByIdAreaEmployeeAndIdBusiness_IdBusiness(String idAreaEmployee, String idBusiness);

    // (opcional) evitar duplicados: mismo empleado asignado a misma área en la misma empresa
    boolean existsByIdArea_IdAreaAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(String idArea, String idEmployee, String idBusiness);

    List<EntityAreaEmployee> findByIdArea_IdAreaAndIdBusiness_IdBusiness(String idArea, String idBusiness);
}
