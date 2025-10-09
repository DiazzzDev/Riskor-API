package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryTrainingEmployee extends JpaRepository<EntityTrainingEmployee, String> {
    Optional<EntityTrainingEmployee> findByIdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String idBusiness);

    List<EntityTrainingEmployee> findByIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(String idEmployee, String idBusiness);

    boolean existsByIdEmployee_IdEmployeeAndIdTraining_IdTrainingAndIdBusiness_IdBusiness(String idEmployee, String idTraining, String idBusiness);

    //PUT - tomar asistencia
    Optional<EntityTrainingEmployee> findByIdTraining_IdTrainingAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(String idTraining, String idEmployee, String idBusiness);

    //Retorna cuántas filas borró - no funca, genera conflictos en fk
    long deleteByIdTraining_IdTrainingAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(String idTraining, String idEmployee, String idBusiness);

    long deleteByIdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String upperCase);

    //Consultas para dashboard:
    List<EntityTrainingEmployee> findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(String idBusiness, String idEmployee);

    List<EntityTrainingEmployee> findByIdBusiness_IdBusiness(String idBusiness);

    boolean existsByIdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String idBusiness);
}
