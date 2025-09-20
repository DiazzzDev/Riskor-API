package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAccidentNotification extends JpaRepository<EntityAccidentNotification, String> {
    //GET por ID de notificación, aislado por empresa
    Optional<EntityAccidentNotification> findByIdAccNotificationAndIdBusiness_IdBusiness(String idAccNotification, String idBusiness);

    //Activas (no borradas y no expiradas) por empresa
    List<EntityAccidentNotification> findByIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(String idBusiness, String isDeleted, LocalDate fromDate);

    //Activas por empleado en la empresa
    List<EntityAccidentNotification> findByIdEmployee_IdEmployeeAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(String idEmployee, String idBusiness, String isDeleted, LocalDate fromDate);

    //Activas por accidente en la empresa
    List<EntityAccidentNotification> findByIdAccident_IdAccidentAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(String idAccident, String idBusiness, String isDeleted, LocalDate fromDate);
}
