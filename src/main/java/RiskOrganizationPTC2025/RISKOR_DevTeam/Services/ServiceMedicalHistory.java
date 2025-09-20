package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMedicalHistory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryMedicalHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceMedicalHistory {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryMedicalHistory objRepoMedicalH;

    @PersistenceContext
    private EntityManager em;

    //Método para retornar una todos el historial médico paginado
    @Transactional(readOnly = true)
    public Page<DTOMedicalHistory> getAllMedicalH(int page, int size, String idBusiness){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityMedicalHistory> historyPage = objRepoMedicalH.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return historyPage.map(this::convertTOMedicalHDTO);
    }

    //Método para obtener paginado por expediente médico (y en su empresa respectiva obviamente)
    @Transactional(readOnly = true)
    public Page<DTOMedicalHistory> getByMedicalRecord(String idMedicalRecord, int page, int size, String idBusiness){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityMedicalHistory> historyPage =objRepoMedicalH.findByIdBusiness_IdBusinessAndIdMedicalRecord_IdMedicalRecord(idBusiness.toUpperCase(), idMedicalRecord, pageable);
        return historyPage.map(this::convertTOMedicalHDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOMedicalHistory postMedicalHistory(@Valid DTOMedicalHistory dtoMedicalHistory, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (dtoMedicalHistory == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityMedicalHistory objMedicalHSaved = objRepoMedicalH.save(convertTOMedicalHEntity(dtoMedicalHistory, idBusiness));
        return convertTOMedicalHDTO(objMedicalHSaved); //Retornamos los valores en formato JSON al controller para la respuesta 201
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOMedicalHistory putMedicalHistory(@Valid DTOMedicalHistory dto, String idMedicalHistory, String idBusiness) {
        //Si el registro ingresado fue nulo, retornamos una excepción
        if (dto == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        // Recuperar validando pertenencia a la empresa (patrón Evidence)
        EntityMedicalHistory entity = objRepoMedicalH
                .findByIdMedicalHistoryAndIdBusiness_IdBusiness(idMedicalHistory, idBusiness.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Historial médico no encontrado"));

        //Añadimos solo los valores presentes (parche seguro)
        if (dto.getMedicalCondition() != null) entity.setMedicalCondition(dto.getMedicalCondition());
        if (dto.getDiagnosisDate() != null) entity.setDiagnosisDate(dto.getDiagnosisDate());
        if (dto.getTreatment() != null) entity.setTreatment(dto.getTreatment());
        if (dto.getTreatmentStartDate() != null) entity.setTreatmentStartDate(dto.getTreatmentStartDate());
        if (dto.getTreatmentEndDate() != null) entity.setTreatmentEndDate(dto.getTreatmentEndDate());

        //Aplicamos lo mismo con las FK, solo que aquí se cambia con ENTITY MANAGER
        if (dto.getIdMedicalStatus() != null && !dto.getIdMedicalStatus().isBlank()) {
            entity.setIdMedicalStatus(em.getReference(EntityMedicalStatus.class, dto.getIdMedicalStatus()));
        }
        if (dto.getIdMedicalRecord() != null && !dto.getIdMedicalRecord().isBlank()) {
            entity.setIdMedicalRecord(em.getReference(EntityMedicalRecord.class, dto.getIdMedicalRecord()));
        }

        //Sin save debido que JPA sincroniza por uso de @Transactional
        return convertTOMedicalHDTO(entity);
    }


    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteMedicalHistory(String idMedicalHistory, String idBusiness){
        //Si el ID del historial médico no es especificado o ingresado, retornamos una excepción
        if (idMedicalHistory == null || idMedicalHistory.trim().isEmpty()) throw new IllegalArgumentException("El ID del Historial Médico no puede ser nulo o vacío");

        //Realizamos una eliminación OPTIMA ya que en el repo indicamos que se elimine específicamente
        //el campo solamente si lo encuentra y PERTENECE a la empresa indicada
        long rows = objRepoMedicalH.deleteByIdMedicalHistoryAndIdBusiness_IdBusiness(idMedicalHistory, idBusiness.toUpperCase());

        if (rows == 0) throw new EntityNotFoundException("Historial médico no encontrado");
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOMedicalHistory convertTOMedicalHDTO(EntityMedicalHistory medicalHistory){
        DTOMedicalHistory objMedicalHDTO = new DTOMedicalHistory();
        objMedicalHDTO.setIdMedicalHistory(medicalHistory.getIdMedicalHistory());
        objMedicalHDTO.setMedicalCondition(medicalHistory.getMedicalCondition());
        objMedicalHDTO.setDiagnosisDate(medicalHistory.getDiagnosisDate());
        objMedicalHDTO.setTreatment(medicalHistory.getTreatment());
        objMedicalHDTO.setTreatmentStartDate(medicalHistory.getTreatmentStartDate());
        objMedicalHDTO.setTreatmentEndDate(medicalHistory.getTreatmentEndDate());

        //IDs de relaciones - Se hace así por uso de lazy fetch con Entity Manager
        objMedicalHDTO.setIdMedicalStatus(medicalHistory.getIdMedicalStatus().getIdMedicalStatus());
        objMedicalHDTO.setIdMedicalRecord(medicalHistory.getIdMedicalRecord().getIdMedicalRecord());
        objMedicalHDTO.setIdBusiness(medicalHistory.getIdBusiness().getIdBusiness());

        return objMedicalHDTO;
    }

    private EntityMedicalHistory convertTOMedicalHEntity(DTOMedicalHistory dtoMedicalHistory, String idBusiness) {
        EntityMedicalHistory history = new EntityMedicalHistory();
        history.setMedicalCondition(dtoMedicalHistory.getMedicalCondition());
        history.setDiagnosisDate(dtoMedicalHistory.getDiagnosisDate());
        history.setTreatment(dtoMedicalHistory.getTreatment());
        history.setTreatmentStartDate(dtoMedicalHistory.getTreatmentStartDate());
        history.setTreatmentEndDate(dtoMedicalHistory.getTreatmentEndDate());

        //Relaciones perezosas por ID con Entity manager
        history.setIdMedicalStatus(em.getReference(EntityMedicalStatus.class, dtoMedicalHistory.getIdMedicalStatus()));
        history.setIdMedicalRecord(em.getReference(EntityMedicalRecord.class, dtoMedicalHistory.getIdMedicalRecord()));
        history.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

        return history;
    }
}