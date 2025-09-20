package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryTrainingRating extends JpaRepository<EntityTrainingRating, String> {
    //Lectura aislada por empresa
    Optional<EntityTrainingRating> findByIdTrainingRatingAndIdBusiness_IdBusiness(String idTrainingRating, String idBusiness);

    // Listar ratings por registro TrainingEmployee en una empresa
    List<EntityTrainingRating> findByIdTrainingEmployee_IdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String idBusiness);

    //Validación - TrainingEmployee solo puede tener 1 rating (si la aplicas)
    boolean existsByIdTrainingEmployee_IdTrainingEmployeeAndIdBusiness_IdBusiness(String idTrainingEmployee, String idBusiness);

    //Borrado aislado por empresa (más eficiente que cargar y borrar)
    long deleteByIdTrainingRatingAndIdBusiness_IdBusiness(String idTrainingRating, String idBusiness);

    //Consultas para dashboard
    List<EntityTrainingRating> findByIdBusiness_IdBusinessAndIdTrainingEmployee_IdTrainingEmployeeIn(String idBusiness, List<String> teIds);
}