package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypeCategoryControlSD;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypeCategoryControlSD;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTypeCategoryControlSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTypeCategoryControlSD {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryTypeCategoryControlSD objRepoTCCSD;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOTypeCategoryControlSD> getAllTypeCategoryCSD(){
        List<EntityTypeCategoryControlSD> objGetTypeCCSD = objRepoTCCSD.findAll();
        return objGetTypeCCSD.stream().map(this::convertTOTypeCCSDDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOTypeCategoryControlSD convertTOTypeCCSDDTO(EntityTypeCategoryControlSD typeCCSD){
        DTOTypeCategoryControlSD objTypeCCSDDTO = new DTOTypeCategoryControlSD();
        objTypeCCSDDTO.setIdTypeCategoryCSD(typeCCSD.getIdTypeCategoryCSD());
        objTypeCCSDDTO.setTypeCategoryCSD(typeCCSD.getTypeCategoryCSD());
        return objTypeCCSDDTO;
    }
}
