package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingRating;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTrainingRating;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTrainingRating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTrainingRating {
    @Autowired
    private RepositoryTrainingRating objRepoTR;

    @Autowired
    private RepositoryTrainingEmployee objRepoTE;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public DTOTrainingRating rating(String idTrainingRating, String idBusiness) {
        EntityTrainingRating rating = objRepoTR.findByIdTrainingRatingAndIdBusiness_IdBusiness(idTrainingRating, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Calificación no encontrada"));
        return convertToDTOTR(rating);
    }

    @Transactional(readOnly = true)
    public List<DTOTrainingRating> listByTrainingEmployee(String idTrainingEmployee, String idBusiness) {
        boolean teExists = objRepoTE.existsByIdTrainingEmployeeAndIdBusiness_IdBusiness(idTrainingEmployee, idBusiness.toUpperCase());
        if (!teExists) {
            throw new jakarta.persistence.EntityNotFoundException("Registro de participación en capacitación no encontrado");
        }

        List<EntityTrainingRating> ratings = objRepoTR.findByIdTrainingEmployee_IdTrainingEmployeeAndIdBusiness_IdBusiness(idTrainingEmployee, idBusiness.toUpperCase());

        return ratings.stream().map(this::convertToDTOTR).collect(Collectors.toList());
    }

    public DTOTrainingRating postTrainingRating(@Valid DTOTrainingRating dtoTR, String idBusiness){
        if (dtoTR == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        boolean alreadyRated = objRepoTR.existsByIdTrainingEmployee_IdTrainingEmployeeAndIdBusiness_IdBusiness(dtoTR.getIdTrainingEmployee(), idBusiness.toUpperCase());
        if (alreadyRated) throw new IllegalArgumentException("El empleado ya calificó la capacitación");


        EntityTrainingRating saved = objRepoTR.save(convertToETR(dtoTR, idBusiness.toUpperCase()));
        return convertToDTOTR(saved);
    }

    public DTOTrainingRating putTrainingRating(@Valid DTOTrainingRating dtoTE, String idTrainingRating, String idBusiness) {
        EntityTrainingRating rating = objRepoTR.findByIdTrainingRatingAndIdBusiness_IdBusiness(idTrainingRating, idBusiness.toUpperCase()).orElseThrow(() -> new EntityNotFoundException("Calificación no encontrada"));

        rating.setRatingTraining(dtoTE.getRatingTraining());
        return convertToDTOTR(rating);
    }

    public boolean removeTrainingRating(String idTrainingRating, String idBusiness) {
        //Hacemos una eliminación con métodos de consulta derivados
        long rows = objRepoTR.deleteByIdTrainingRatingAndIdBusiness_IdBusiness(idTrainingRating, idBusiness.toUpperCase());

        if (rows == 0) throw new EntityNotFoundException("Calificación no encontrada");
        return true;
    }

    private DTOTrainingRating convertToDTOTR(EntityTrainingRating trainingRating){
        DTOTrainingRating objDTOTR = new DTOTrainingRating();
        objDTOTR.setIdTrainingRating(trainingRating.getIdTrainingRating());
        objDTOTR.setRatingTraining(trainingRating.getRatingTraining());
        objDTOTR.setIdTrainingEmployee(trainingRating.getIdTrainingEmployee() != null ? trainingRating.getIdTrainingEmployee().getIdTrainingEmployee() : null);
        objDTOTR.setIdBusiness(trainingRating.getIdBusiness() != null ? trainingRating.getIdBusiness().getIdBusiness() : null);

        return objDTOTR;
    }

    private EntityTrainingRating convertToETR(DTOTrainingRating dtoTR, String idBusiness){
        EntityTrainingRating trainingRating = new EntityTrainingRating();
        trainingRating.setRatingTraining(dtoTR.getRatingTraining());
        trainingRating.setIdTrainingEmployee(em.getReference(EntityTrainingEmployee.class, dtoTR.getIdTrainingEmployee()));
        trainingRating.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness));

        return trainingRating;
    }
}