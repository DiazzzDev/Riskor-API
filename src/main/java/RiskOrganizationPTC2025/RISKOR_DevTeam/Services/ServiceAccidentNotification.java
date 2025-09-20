package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentNotification;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentNotification;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentNotification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceAccidentNotification {
    @Autowired
    private RepositoryAccidentNotification objRepoAN;

    @Transactional(readOnly = true)
    public DTOAccidentNotification getById(String idAccNotification, String idBusiness) {
        EntityAccidentNotification notification = objRepoAN.findByIdAccNotificationAndIdBusiness_IdBusiness(idAccNotification, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));
        return convertToDTO(notification);
    }

    @Transactional(readOnly = true)
    public List<DTOAccidentNotification> listActiveByBusiness(String idBusiness) {
        List<EntityAccidentNotification> notifications = objRepoAN.findByIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(idBusiness.toUpperCase(), "N", LocalDate.now());
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DTOAccidentNotification> listActiveByEmployee(String idEmployee, String idBusiness) {
        List<EntityAccidentNotification> notifications = objRepoAN.findByIdEmployee_IdEmployeeAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(idEmployee, idBusiness.toUpperCase(), "N", LocalDate.now());
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DTOAccidentNotification> listActiveByAccident(String idAccident, String idBusiness) {
        return objRepoAN.findByIdAccident_IdAccidentAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(idAccident, idBusiness.toUpperCase(), "N", LocalDate.now()).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private DTOAccidentNotification convertToDTO(EntityAccidentNotification notifications){
        DTOAccidentNotification dtoAN = new DTOAccidentNotification();
        dtoAN.setIdAccNotification(notifications.getIdAccNotification());
        dtoAN.setMessage(notifications.getMessage());
        dtoAN.setNotificationDate(notifications.getNotificationDate());
        dtoAN.setExpirationDate(notifications.getExpirationDate());
        dtoAN.setIsDeleted(notifications.getIsDeleted());
        dtoAN.setCreationDate(notifications.getCreationDate());
        dtoAN.setIdEmployee(notifications.getIdEmployee() != null ? notifications.getIdEmployee().getIdEmployee() : null);
        dtoAN.setIdAccident(notifications.getIdAccident() != null ? notifications.getIdAccident().getIdAccident() : null);
        dtoAN.setIdBusiness(notifications.getIdBusiness() != null ? notifications.getIdBusiness().getIdBusiness() : null);

        return dtoAN;
    }
}