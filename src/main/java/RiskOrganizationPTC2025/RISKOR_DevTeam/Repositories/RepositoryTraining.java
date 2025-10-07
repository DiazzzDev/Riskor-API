package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTraining;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryTraining extends JpaRepository<EntityTraining, String> {
    //Buscar por título (Sin importar mayúsculas o minúsculas) aislado por empresa

    Page<EntityTraining> findByTitleContainingIgnoreCaseAndIdBusiness_IdBusiness(String title, String idBusiness, Pageable pageable);

    //Paginado de TODAS las capacitaciones de una empresa
    Page<EntityTraining> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    // Leer/editar/borrar aislado por empresa
    Optional<EntityTraining> findByIdTrainingAndIdBusiness_IdBusiness(String idTraining, String idBusiness);

    //Training
    List<EntityTraining> findByIdBusiness_IdBusinessAndIdTrainingIn(String idBusiness, List<String> idTrainings);

    @Query("""
        SELECT t
        FROM EntityTraining t
        WHERE t.idBusiness.idBusiness = :idBusiness
          AND EXISTS (
            SELECT 1
            FROM EntityTrainingEmployee te
            WHERE te.idTraining.idTraining = t.idTraining
              AND te.idEmployee.idEmployee = :idEmployee
              AND te.idBusiness.idBusiness = :idBusiness
          )
          AND (
                t.trainingDate > :today
             OR (t.trainingDate = :today AND t.startHour >= :now)
          )
        ORDER BY t.trainingDate ASC, t.startHour ASC
    """)
    List<EntityTraining> findNextTrainingForEmployee(
            @Param("idBusiness") String idBusiness,
            @Param("idEmployee") String idEmployee,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now,
            Pageable pageable
    );
}
