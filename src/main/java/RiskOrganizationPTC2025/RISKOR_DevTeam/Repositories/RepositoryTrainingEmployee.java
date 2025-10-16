package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryTrainingEmployee extends JpaRepository<EntityTrainingEmployee, String> {
    Optional<EntityTrainingEmployee> findByIdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String idBusiness);

    List<EntityTrainingEmployee> findByIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(String idEmployee, String idBusiness);

    boolean existsByIdEmployee_IdEmployeeAndIdTraining_IdTrainingAndIdBusiness_IdBusiness(String idEmployee, String idTraining, String idBusiness);

    //PUT - tomar asistencia
    Optional<EntityTrainingEmployee> findByIdTraining_IdTrainingAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(String idTraining, String idEmployee, String idBusiness);

    @Modifying
    @Transactional
    @Query("DELETE FROM EntityTrainingEmployee e " +
            "WHERE e.idTraining.idTraining = :idTraining " +
            "AND e.idEmployee.idEmployee = :idEmployee " +
            "AND e.idBusiness.idBusiness = :idBusiness")
    int deleteByTrainingAndEmployeeAndBusiness(@Param("idTraining") String idTraining,
                                               @Param("idEmployee") String idEmployee,
                                               @Param("idBusiness") String idBusiness);

    @Modifying
    @Transactional
    @Query("DELETE FROM EntityTrainingEmployee e " +
            "WHERE e.idTrainingEmployee = :idTrainingEmployee " +
            "AND e.idBusiness.idBusiness = :idBusiness")
    int deleteByTrainingEmployeeAndBusiness(@Param("idTrainingEmployee") String idTrainingEmployee,
                                            @Param("idBusiness") String idBusiness);

    //Consultas para dashboard:
    List<EntityTrainingEmployee> findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(String idBusiness, String idEmployee);

    List<EntityTrainingEmployee> findByIdBusiness_IdBusiness(String idBusiness);

    boolean existsByIdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String idBusiness);

    List<EntityTrainingEmployee> findByIdTraining_IdTrainingAndIdBusiness_IdBusiness(String idTraining, String upperCase);
}
