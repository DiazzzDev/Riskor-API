package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataDuplicate;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMedicalRecord;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryMedicalRecord;
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
import java.time.LocalDate;

@Service
@Transactional
public class ServiceMedicalRecord {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryMedicalRecord objRepoMedicalR;

    @PersistenceContext
    private EntityManager em;

    //GET PRINCIPAL - TODOS LOS REGISTROS POR EMPRESA
    @Transactional(readOnly = true)
    public Page<DTOMedicalRecord> getAllMedicalR(int page, int size, String idBusiness){
        Pageable pageable = PageRequest.of(page, size);
        return objRepoMedicalR.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable).map(this::convertTOMedicalRDTO);
    }

    //GET paginado por empleado
    @Transactional(readOnly = true)
    public Page<DTOMedicalRecord> getMedicalRByEmployee(String idEmployee, int page, int size, String idBusiness){
        Pageable pageable = PageRequest.of(page, size);
        return objRepoMedicalR.findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(idBusiness.toUpperCase(), idEmployee, pageable).map(this::convertTOMedicalRDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOMedicalRecord postMedicalRecord(@Valid DTOMedicalRecord dtoMedR, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (dtoMedR == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Validamos si el empleado ya tiene un registro médico
        if (objRepoMedicalR.existsByIdEmployee_IdEmployee(dtoMedR.getIdEmployee())) throw new ExceptionDataDuplicate("El empleado ya tiene un expediente médico");

        EntityMedicalRecord record = new EntityMedicalRecord();
        record.setAllergie(dtoMedR.getAllergie());
        record.setContactName(dtoMedR.getContactName());
        record.setContactPhone(dtoMedR.getContactPhone());
        record.setSpecialNeed(dtoMedR.getSpecialNeed());

        //Asignamos estos valores desde aquí
        record.setCreationDate(LocalDate.now());
        record.setLastUpdate(LocalDate.now());

        //Referencias “perezosas” por su ID ahora son guardadas en la entidad
        record.setIdBloodType(em.getReference(EntityBloodType.class, dtoMedR.getIdBloodType()));
        record.setIdEmployee(em.getReference(EntityEmployee.class, dtoMedR.getIdEmployee()));
        record.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityMedicalRecord recordSaved = objRepoMedicalR.save(record);
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOMedicalRDTO(recordSaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOMedicalRecord putMedicalRecord(@Valid DTOMedicalRecord dtoMedR, String idMedicalRecord, String idBusiness) {
        //Si el registro ingresado fue nulo, retornamos una excepción
        if(dtoMedR == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Verificamos existencia del elemento y lo guardamos en una entidad
        EntityMedicalRecord record = objRepoMedicalR.findById(idMedicalRecord).orElseThrow(() -> new EntityNotFoundException("Expediente médico no encontrado con ID: " + idMedicalRecord));

        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error (Luego se debe crear excepción personalizada)
        if (!record.getIdBusiness().getIdBusiness().equalsIgnoreCase(idBusiness)) {
            throw new EntityNotFoundException("El registro no pertenece a la empresa indicada");
        }

        //Añadimos los valores
        record.setAllergie(dtoMedR.getAllergie());
        record.setContactName(dtoMedR.getContactName());
        record.setContactPhone(dtoMedR.getContactPhone());
        record.setSpecialNeed(dtoMedR.getSpecialNeed());

        //record.setIdBloodType(dtoMedR.getIdBloodType());
        if (dtoMedR.getIdBloodType() != null && !dtoMedR.getIdBloodType().isBlank()) {
            record.setIdBloodType(em.getReference(EntityBloodType.class, dtoMedR.getIdBloodType()));
        }

        //No cambiamos empleado ni empresa en el PUT (evita romper UNIQUE y la pertenencia)
        record.setLastUpdate(LocalDate.now()); //Agregamos para actualizar el registro médico
        return convertTOMedicalRDTO(record);
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOMedicalRecord convertTOMedicalRDTO(EntityMedicalRecord medicalRecord){
        DTOMedicalRecord dto = new DTOMedicalRecord();
        dto.setIdMedicalRecord(medicalRecord.getIdMedicalRecord());
        dto.setAllergie(medicalRecord.getAllergie());
        dto.setContactName(medicalRecord.getContactName());
        dto.setContactPhone(medicalRecord.getContactPhone());
        dto.setSpecialNeed(medicalRecord.getSpecialNeed());
        dto.setCreationDate(medicalRecord.getCreationDate());
        dto.setLastUpdate(medicalRecord.getLastUpdate());

        // extraer IDs de las relaciones
        dto.setIdBloodType(medicalRecord.getIdBloodType().getIdBloodType());
        dto.setIdEmployee(medicalRecord.getIdEmployee().getIdEmployee());
        dto.setIdBusiness(medicalRecord.getIdBusiness().getIdBusiness());

        return dto;
    }
}