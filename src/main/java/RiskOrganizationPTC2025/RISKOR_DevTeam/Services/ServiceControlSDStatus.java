package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityControlSDStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOControlSDStatus;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryControlSDStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceControlSDStatus {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryControlSDStatus objRepoCSDS;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOControlSDStatus> getAllControlSDStatus(){
        List<EntityControlSDStatus> objGetControlSDS = objRepoCSDS.findAll();
        return objGetControlSDS.stream().map(this::convertTOControlSDSDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOControlSDStatus convertTOControlSDSDTO(EntityControlSDStatus controlSDS){
        DTOControlSDStatus objControlSDSDTO = new DTOControlSDStatus();
        objControlSDSDTO.setIdControlSDStatus(controlSDS.getIdControlSDStatus());
        objControlSDSDTO.setNameControlStatus(controlSDS.getNameControlStatus());
        return objControlSDSDTO;
    }
}
