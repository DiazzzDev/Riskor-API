package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityInspectionType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOInspectionType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryInspectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceInspectionType {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryInspectionType objRepoIT;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOInspectionType> getAllInspectionT(){
        List<EntityInspectionType> objGetInspectionT = objRepoIT.findAll();
        return objGetInspectionT.stream().map(this::convertTOInspectionTDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOInspectionType convertTOInspectionTDTO(EntityInspectionType inspectionT){
        DTOInspectionType objInspectionTDTO = new DTOInspectionType();
        objInspectionTDTO.setIdInspectionType(inspectionT.getIdInspectionType());
        objInspectionTDTO.setInspectionType(inspectionT.getInspectionType());
        return objInspectionTDTO;
    }
}
