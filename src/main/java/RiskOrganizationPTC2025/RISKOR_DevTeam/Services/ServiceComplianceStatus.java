package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityComplianceStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOComplianceStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryComplianceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceComplianceStatus {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryComplianceStatus objRepoCS;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOComplianceStatus> getAllComplianceS(){
        List<EntityComplianceStatus> objGetComplianceS = objRepoCS.findAll();
        return objGetComplianceS.stream().map(this::convertTOComplianceSDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOComplianceStatus convertTOComplianceSDTO(EntityComplianceStatus complianceS){
        DTOComplianceStatus objComplianceSDTO = new DTOComplianceStatus();
        objComplianceSDTO.setIdComplianceStatus(complianceS.getIdComplianceStatus());
        objComplianceSDTO.setComplianceStatus(complianceS.getComplianceStatus());
        return objComplianceSDTO;
    }
}
