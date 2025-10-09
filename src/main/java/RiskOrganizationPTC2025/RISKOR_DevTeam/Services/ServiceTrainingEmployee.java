package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingEmployee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTrainingEmployee {
    @Autowired
    private RepositoryTrainingEmployee objRepoTE;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ServiceEmailSender serviceEmailSender;

    @Transactional(readOnly = true)
    public DTOTrainingEmployee getTrainingEmployeeById(String idTrainingEmployee, String idBusiness) {
        EntityTrainingEmployee entityTrainingEmployee = objRepoTE.findByIdTrainingEmployeeAndIdBusiness_IdBusiness(idTrainingEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Registro no encontrado"));
        return convertToDTOTE(entityTrainingEmployee);
    }

    @Transactional(readOnly = true)
    public List<DTOTrainingEmployee> getTrainingEmployeeByEmployee(String idEmployee, String idBusiness) {
        List<EntityTrainingEmployee> employeeList = objRepoTE.findByIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(idEmployee, idBusiness.toUpperCase());
        return employeeList.stream().map(this::convertToDTOTE).collect(Collectors.toList());
    }

    public DTOTrainingEmployee postTrainingEmployee(@Valid DTOTrainingEmployee dtoTE, String idBusiness) {
        if(dtoTE == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Evitamos duplicado empleado-capacitación en la misma empresa
        boolean dup = objRepoTE.existsByIdEmployee_IdEmployeeAndIdTraining_IdTrainingAndIdBusiness_IdBusiness(dtoTE.getIdEmployee(), dtoTE.getIdTraining(), idBusiness.toUpperCase());
        if (dup) throw new IllegalArgumentException("El empleado ya está registrado en esa capacitación");

        EntityTrainingEmployee trainingEmployee = objRepoTE.save(convertToETE(dtoTE, idBusiness));

        try {
            final String startAt = formatStartAt(trainingEmployee.getIdTraining().getTrainingDate(), trainingEmployee.getIdTraining().getStartHour());
            final String subject = "Se te ha unido a la capacitación: " + trainingEmployee.getIdTraining().getTitle();

            serviceEmailSender.sendNewTrainingTemplate(
                    trainingEmployee.getIdEmployee().getEmployeeEmail(),
                    subject,
                    "RISKOR",
                    trainingEmployee.getIdEmployee().getFirstName() + " " + trainingEmployee.getIdEmployee().getLastName(),
                    trainingEmployee.getIdTraining().getTitle(),
                    trainingEmployee.getIdTraining().getDescription(),
                    startAt,
                    trainingEmployee.getIdTraining().getTrainingLocation(),
                    trainingEmployee.getIdTraining().getIdTrainingModality().getTrainingModality()
            );
        } catch (Exception emailEx) {
            //Dejamos advertencia
            System.err.println("ADVERTENCIA: el correo de capacitación falló. Detalle: " + emailEx.getMessage());
        }

        return convertToDTOTE(trainingEmployee);
    }

    public DTOTrainingEmployee putTrainingEmployee(@Valid DTOTrainingEmployee dtoTE, String idTrainingEmployee, String idBusiness) {
        if (dtoTE == null) throw new IllegalArgumentException("No puede haber campos vacíos");

        EntityTrainingEmployee trainingEmployee = objRepoTE.findByIdTrainingEmployeeAndIdBusiness_IdBusiness(idTrainingEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Registro no encontrado"));

        //Actualización de campos - En caso de ser nulos mantienen su valor anterior
        trainingEmployee.setAttendance(dtoTE.getAttendance() != null ? dtoTE.getAttendance().trim().toUpperCase() : trainingEmployee.getAttendance());
        if (dtoTE.getAttendanceDate() != null) trainingEmployee.setAttendanceDate(dtoTE.getAttendanceDate());
        trainingEmployee.setObservation(dtoTE.getObservation());
        if (dtoTE.getIdEmployee() != null) trainingEmployee.setIdEmployee(em.getReference(EntityEmployee.class, dtoTE.getIdEmployee()));
        if (dtoTE.getIdTraining() != null) trainingEmployee.setIdTraining(em.getReference(EntityTraining.class, dtoTE.getIdTraining()));

        //Si cambiaron empleado o capacitación, evita duplicados
        boolean dup = objRepoTE.existsByIdEmployee_IdEmployeeAndIdTraining_IdTrainingAndIdBusiness_IdBusiness(trainingEmployee.getIdEmployee().getIdEmployee(), trainingEmployee.getIdTraining().getIdTraining(), idBusiness.toUpperCase());
        if (dup && !trainingEmployee.getIdTrainingEmployee().equals(idTrainingEmployee)) {
            throw new IllegalArgumentException("El empleado ya está registrado en esa capacitación");
        }
        return convertToDTOTE(trainingEmployee);  //JPA sincroniza por @Transactional
    }

    public DTOTrainingEmployee takeAttendance(DTOTrainingEmployee dtoTE, String idTraining, String idEmployee, String idBusiness) {
        if (dtoTE == null) throw new IllegalArgumentException("No puede haber campos vacíos");

        EntityTrainingEmployee trainingEmployee = objRepoTE.findByIdTraining_IdTrainingAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(idTraining, idEmployee, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado dentro de esta capacitación"));

        //Obtenemos su asistencia y la actualizamos con el dato recibido en el JSON
        if (dtoTE.getAttendance() != null) {
            String attendance = dtoTE.getAttendance().trim().toUpperCase();
            //Validamos que la asistencia sean los carácteres esperados por la DB - Es otra capa de seguridad porque ya existe el REGEX en DTO
            if (!attendance.equals("S") && !attendance.equals("N")) throw new IllegalArgumentException("La asistencia debe ser 'S' o 'N'");
            trainingEmployee.setAttendance(attendance);
        }
        trainingEmployee.setObservation(dtoTE.getObservation()); //Aquí se obtiene la observación recibida
        trainingEmployee.setAttendanceDate(LocalDate.now()); //Seteamos la asistencia como hoy

        return convertToDTOTE(trainingEmployee); //No se necesita guardar creando otra entidad por que ya hacemos uso de save
    }

    public boolean removeEmployeeFromTraining(String idTraining, String idEmployee, String idBusiness) {
        long rows = objRepoTE.deleteByIdTraining_IdTrainingAndIdEmployee_IdEmployeeAndIdBusiness_IdBusiness(idTraining, idEmployee, idBusiness.toUpperCase());

        //Evitamos doble consulta y eliminamos, si no lo encuentra y elimina las filas afectadas serán 0 y devolverá false
        if(rows != 0) return true;

        return false; //Si se retornó false significa que no se pudo eliminar
    }

    public boolean removeTrainingEmployee(String idTrainingEmployee, String idBusiness) {
        //Como ya posee validaciones desde la DB con múltiples CONSTRAINT se las aplicamos desde el nombre del método de la JPA
        //Para eliminar de manera eficiente y segura
        long rows = objRepoTE.deleteByIdTrainingEmployeeAndIdBusiness_IdBusiness(idTrainingEmployee, idBusiness.toUpperCase());

        if (rows == 0) throw new EntityNotFoundException("Registro no encontrado"); //Si al momento de eliminar una fila devolvió 0 va a mandar error
        return true;
    }

    private DTOTrainingEmployee convertToDTOTE(EntityTrainingEmployee trainingEmployee){
        DTOTrainingEmployee dtoTE = new DTOTrainingEmployee();
        dtoTE.setIdTrainingEmployee(trainingEmployee.getIdTrainingEmployee());
        dtoTE.setAttendance(trainingEmployee.getAttendance());
        dtoTE.setAttendanceDate(trainingEmployee.getAttendanceDate());
        dtoTE.setObservation(trainingEmployee.getObservation());
        dtoTE.setIdEmployee(trainingEmployee.getIdEmployee() != null ? trainingEmployee.getIdEmployee().getIdEmployee() : null);
        dtoTE.setIdTraining(trainingEmployee.getIdTraining() != null ? trainingEmployee.getIdTraining().getIdTraining() : null);
        dtoTE.setIdBusiness(trainingEmployee.getIdBusiness() != null ? trainingEmployee.getIdBusiness().getIdBusiness() : null);

        return dtoTE;
    }

    private EntityTrainingEmployee convertToETE(DTOTrainingEmployee dtoTE, String idBusiness){
        EntityTrainingEmployee trainingEmployee = new EntityTrainingEmployee();
        trainingEmployee.setAttendance(dtoTE.getAttendance() != null ? dtoTE.getAttendance().trim().toUpperCase() : null);
        trainingEmployee.setAttendanceDate(dtoTE.getAttendanceDate());
        trainingEmployee.setObservation(dtoTE.getObservation());
        trainingEmployee.setIdEmployee(em.getReference(EntityEmployee.class, dtoTE.getIdEmployee()));
        trainingEmployee.setIdTraining(em.getReference(EntityTraining.class, dtoTE.getIdTraining()));
        trainingEmployee.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return trainingEmployee;
    }

    //Método que se encarga de dar el formato deseado a la plantilla que será enviada por correo
    private String formatStartAt(java.time.LocalDate date, java.time.LocalTime time) {
        if (date == null || time == null) return "";
        var ldt = java.time.LocalDateTime.of(date, time);
        var fmt = java.time.format.DateTimeFormatter.ofPattern(
                "d 'de' MMMM 'de' uuuu, hh:mm a",
                new java.util.Locale("es", "ES")
        );
        return ldt.format(fmt);
    }
}