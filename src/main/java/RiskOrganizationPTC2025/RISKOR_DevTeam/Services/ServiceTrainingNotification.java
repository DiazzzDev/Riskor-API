package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingNotification;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingNotification;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingNotification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) //Todos los métodos de esta clase van a ser 100% solo para lectura
public class ServiceTrainingNotification {
    @Autowired
    private RepositoryTrainingNotification repo;

    @Transactional(readOnly = true)
    public DTOTrainingNotification getById(String idTrnNotification, String idBusiness) {
        EntityTrainingNotification notification = repo.findByIdTrnNotificationAndIdBusiness_IdBusiness(idTrnNotification, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));
        return convertToDTO(notification);
    }

    @Transactional(readOnly = true)
    public List<DTOTrainingNotification> listActiveByBusiness(String idBusiness) {
        List<EntityTrainingNotification> notifications = repo.findByIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(idBusiness.toUpperCase(), "N", LocalDate.now());
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DTOTrainingNotification> listActiveByEmployee(String idEmployee, String idBusiness) {
        List<EntityTrainingNotification> notifications = repo.findByIdEmployee_IdEmployeeAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(idEmployee, idBusiness.toUpperCase(), "N", LocalDate.now());
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DTOTrainingNotification> listActiveByTraining(String idTraining, String idBusiness) {
        List<EntityTrainingNotification> notifications = repo.findByIdTraining_IdTrainingAndIdBusiness_IdBusinessAndIsDeletedAndExpirationDateGreaterThanEqual(idTraining, idBusiness.toUpperCase(), "N", LocalDate.now());
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private DTOTrainingNotification convertToDTO(EntityTrainingNotification e) {
        DTOTrainingNotification dto = new DTOTrainingNotification();
        dto.setIdTrnNotification(e.getIdTrnNotification());
        dto.setMessage(e.getMessage());
        dto.setNotificationDate(e.getNotificationDate());
        dto.setExpirationDate(e.getExpirationDate());
        dto.setIsDeleted(e.getIsDeleted());
        dto.setCreationDate(e.getCreationDate());
        dto.setIdEmployee(e.getIdEmployee() != null ? e.getIdEmployee().getIdEmployee() : null);
        dto.setIdTraining(e.getIdTraining() != null ? e.getIdTraining().getIdTraining() : null);
        dto.setIdBusiness(e.getIdBusiness() != null ? e.getIdBusiness().getIdBusiness() : null);

        return dto;
    }
}
