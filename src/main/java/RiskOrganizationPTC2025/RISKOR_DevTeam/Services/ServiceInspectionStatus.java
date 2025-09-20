package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspectionStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspectionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceInspectionStatus {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryInspectionStatus objRepoIS;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOInspectionStatus> getAllInspectionS(){
        List<EntityInspectionStatus> objGetInspectionS = objRepoIS.findAll();
        return objGetInspectionS.stream().map(this::convertTOInspectionSDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOInspectionStatus convertTOInspectionSDTO(EntityInspectionStatus inspectionS){
        DTOInspectionStatus objInspectionSDTO = new DTOInspectionStatus();
        objInspectionSDTO.setIdInspectionStatus(inspectionS.getIdInspectionStatus());
        objInspectionSDTO.setInspectionStatus(inspectionS.getInspectionStatus());
        return objInspectionSDTO;
    }
}
