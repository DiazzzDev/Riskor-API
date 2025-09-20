package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityMedicalStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOMedicalStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryMedicalStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceMedicalStatus {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryMedicalStatus objRepoMS;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOMedicalStatus> getAllMedicalStatus(){
        List<EntityMedicalStatus> objGetMedicalS = objRepoMS.findAll();
        return objGetMedicalS.stream().map(this::convertTOMedicalSDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOMedicalStatus convertTOMedicalSDTO(EntityMedicalStatus medicalS){
        DTOMedicalStatus objMedicalSDTO = new DTOMedicalStatus();
        objMedicalSDTO.setIdMedicalStatus(medicalS.getIdMedicalStatus());
        objMedicalSDTO.setMedicalStatus(medicalS.getMedicalStatus());
        return objMedicalSDTO;
    }
}
