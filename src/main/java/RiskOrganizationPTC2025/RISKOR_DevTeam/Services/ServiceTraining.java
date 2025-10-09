package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTraining;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingModality;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTraining;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTraining;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingEmployee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTraining {
    @Autowired
    private RepositoryTraining objRepoT;

    @Autowired
    private RepositoryTrainingEmployee objRepoTE;

    @PersistenceContext
    private EntityManager em; //Ayuda a evitar cargar objetos completos en FK

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional(readOnly = true)
    public DTOTraining getNextTrainingForEmployee(String idBusiness, String idEmployee) {
        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();

        //Se utiliza paginación a pesar que devuelve un solo elemento debido a temas de búsqueda SQL para mejorar rendimiento al momento de buscar
        Pageable limitOne = PageRequest.of(0, 1, Sort.by(Sort.Order.asc("trainingDate"), Sort.Order.asc("startHour")));

        List<EntityTraining> nextTrainingForEmployee = objRepoT.findNextTrainingForEmployee(
                idBusiness.toUpperCase(),
                idEmployee.toUpperCase(),
                today, now,
                limitOne
        );

        if (nextTrainingForEmployee.isEmpty()) throw new EntityNotFoundException("No hay capacitaciones próximas para el empleado.");

        //Mandamos el primer elemento encontrado
        return convertToDTOT(nextTrainingForEmployee.get(0));
    }

    @Transactional(readOnly = true) //Validación de transactional que va a evitar que se pueda llegar a realizar un ataque al escribir en los datos que se mandan
    public Page<DTOTraining> getTrainingByTitle(int page, int size, String title, String idBusiness) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("El título es requerido");

        Pageable pageable = PageRequest.of(page, size);
        Page<EntityTraining> trainings = objRepoT.findByTitleContainingIgnoreCaseAndIdBusiness_IdBusiness(title.trim(), idBusiness.toUpperCase(), pageable);
        return trainings.map(this::convertToDTOT);
    }

    @Transactional(readOnly = true)
    public DTOTraining getTrainingById(String idTraining, String idBusiness) {
        EntityTraining training = objRepoT.findByIdTrainingAndIdBusiness_IdBusiness(idTraining, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Capacitación no encontrada con ID: " + idTraining));
        return convertToDTOT(training);
    }

    //Método para móvil dashboard
    @Transactional(readOnly = true)
    public List<DTOTraining> getTrainingsByEmployee(String idEmployee, String idBusiness) {
        //Primero buscamos todas las capacitaciones asociadas a un empleado
        List<EntityTrainingEmployee> trainingEmployee = objRepoTE.findByIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness);

        if (trainingEmployee.isEmpty()) return Collections.emptyList();

        //Crea una nueva lista que solo contendrá los IDs de las capacitaciones
        List<String> trainingIds = trainingEmployee.stream().map(te -> te.getIdTraining().getIdTraining()).collect(Collectors.toList());

        //Obtener todas las capacitaciones correspondientes a esos IDs
        List<EntityTraining> trainings = objRepoT.findAllById(trainingIds);

        //Convertimos la lista de entidades a DTO
        return trainings.stream().map(this::convertToDTOT).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<DTOTraining> getAllTrainings(int page, int size, String idBusiness){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityTraining> trainings = objRepoT.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return trainings.map(this::convertToDTOT);
    }

    public DTOTraining postTraining(DTOTraining dtoT, String idBusiness){
        if (dtoT == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityTraining training = objRepoT.save(convertToET(dtoT, idBusiness.toUpperCase()));
        return convertToDTOT(training);
    }

    public DTOTraining putTraining(DTOTraining dtoT, String idTraining, String idBusiness) {
        if(dtoT == null) throw new IllegalArgumentException("No pueden haber campos vacíos");
        //Se crea un elemento de la entidad donde verifica si existe el Registro que se va a actualizar, si no existe lanza error (Luego se debe crear excepción personalizada)
        EntityTraining training = objRepoT.findByIdTrainingAndIdBusiness_IdBusiness(idTraining, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Capacitación no encontrada con ID: " + idTraining));

        training.setTitle(dtoT.getTitle());
        training.setDescription(dtoT.getDescription());
        training.setFullNameTraining(dtoT.getFullNameTraining());
        training.setRequestDate(dtoT.getRequestDate());
        training.setTrainingDate(dtoT.getTrainingDate());

        LocalTime start = LocalTime.parse(dtoT.getStartHour(), HH_MM);
        LocalTime end = LocalTime.parse(dtoT.getEndHour(), HH_MM);
        validateTimes(start, end);

        training.setStartHour(start);
        training.setEndHour(end);
        training.setDurationHour(formatOracleInterval(start, end));
        training.setTrainingLocation(dtoT.getTrainingLocation());
        if (dtoT.getIdTrainingModality() != null) {
            training.setIdTrainingModality(em.getReference(EntityTrainingModality.class, dtoT.getIdTrainingModality()));
        }

        return convertToDTOT(training); //JPA sincroniza por @Transactional, de esta manera podemos ahorrar el uso de SAVE
    }

    public boolean removeTraining(String idTraining, String idBusiness){
        if (idTraining == null || idTraining.trim().isEmpty()) throw new IllegalArgumentException("El ID de la capacitación no puede ser nulo o vacío");

        EntityTraining training = objRepoT.findByIdTrainingAndIdBusiness_IdBusiness(idTraining, idBusiness).orElseThrow(() -> new EntityNotFoundException("No se encontró la capacitación con ID: " + idTraining));
        objRepoT.delete(training);
        return true;
    }

    private DTOTraining convertToDTOT(EntityTraining training){
        DTOTraining dtoT = new DTOTraining();
        dtoT.setIdTraining(training.getIdTraining());
        dtoT.setTitle(training.getTitle());
        dtoT.setDescription(training.getDescription());
        dtoT.setFullNameTraining(training.getFullNameTraining());
        dtoT.setRequestDate(training.getRequestDate());
        dtoT.setTrainingDate(training.getTrainingDate());
        dtoT.setStartHour(training.getStartHour() != null ? training.getStartHour().format(HH_MM) : null);
        dtoT.setEndHour(training.getEndHour() != null ? training.getEndHour().format(HH_MM) : null);

        // Duración bonita para el front (HH:mm)
        if (training.getStartHour() != null && training.getEndHour() != null) {
            Duration d = Duration.between(training.getStartHour(), training.getEndHour());
            long hours = d.toHours();
            long minutes = d.toMinutes() % 60;
            dtoT.setDurationHour(String.format("%02d:%02d", hours, minutes));
        } else {
            dtoT.setDurationHour(null);
        }

        dtoT.setTrainingLocation(training.getTrainingLocation());
        dtoT.setIdTrainingModality(training.getIdTrainingModality() != null ? training.getIdTrainingModality().getIdTrainingModality() : null);
        dtoT.setIdBusiness(training.getIdBusiness() != null ? training.getIdBusiness().getIdBusiness() : null);

        return dtoT;
    }

    private EntityTraining convertToET(DTOTraining dtoT, String idBusiness){
        EntityTraining training = new EntityTraining();
        training.setTitle(dtoT.getTitle());
        training.setDescription(dtoT.getDescription());
        training.setFullNameTraining(dtoT.getFullNameTraining());
        training.setRequestDate(dtoT.getRequestDate());
        training.setTrainingDate(dtoT.getTrainingDate());

        LocalTime start = LocalTime.parse(dtoT.getStartHour(), HH_MM);
        LocalTime end = LocalTime.parse(dtoT.getEndHour(), HH_MM);
        validateTimes(start, end);

        training.setStartHour(start);
        training.setEndHour(end);
        training.setDurationHour(formatOracleInterval(start, end));
        training.setTrainingLocation(dtoT.getTrainingLocation());
        training.setIdTrainingModality(em.getReference(EntityTrainingModality.class, dtoT.getIdTrainingModality()));
        training.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

        return training;
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("startHour y endHour son obligatorios (HH:mm)");
        if (!end.isAfter(start)) throw new IllegalArgumentException("endHour debe ser mayor que startHour");
    }

    // Formato INTERVAL DAY(0) TO SECOND que usas en Oracle
    private String formatOracleInterval(LocalTime start, LocalTime end) {
        Duration d = Duration.between(start, end);
        long hours = d.toHours();
        long minutes = d.toMinutes() % 60;
        long seconds = d.getSeconds() % 60;
        return String.format("0 %02d:%02d:%02d.0", hours, minutes, seconds);
    }
}