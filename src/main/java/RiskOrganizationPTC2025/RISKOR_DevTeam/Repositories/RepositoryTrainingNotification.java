package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryTrainingNotification extends JpaRepository<EntityTrainingNotification, String> {
    Optional<EntityTrainingNotification> findByIdTrnNotificationAndIdBusiness_IdBusiness(String idTrnNotification, String idBusiness);

    //Notificaciones activas por empresa
    List<EntityTrainingNotification> findByIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(String idBusiness, String isDeleted, LocalDate fromDate);

    //Notificaciones activas por empleado
    List<EntityTrainingNotification> findByIdEmployee_IdEmployeeAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(String idEmployee, String idBusiness, String isDeleted, LocalDate fromDate);

    //Notificaciones activas por capacitación
    List<EntityTrainingNotification> findByIdTraining_IdTrainingAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(String idTraining, String idBusiness, String isDeleted, LocalDate fromDate);
}
