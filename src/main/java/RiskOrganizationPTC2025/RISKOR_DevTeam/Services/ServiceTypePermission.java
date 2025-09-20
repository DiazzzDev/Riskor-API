package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTypePermission;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOTypePermission;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryTypePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceTypePermission {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryTypePermission objRepoTP;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOTypePermission> getAllTypePermission(){
        List<EntityTypePermission> objGetTypeP = objRepoTP.findAll();
        return objGetTypeP.stream().map(this::convertTOTypePDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOTypePermission convertTOTypePDTO(EntityTypePermission typeP){
        DTOTypePermission objTypePDTO = new DTOTypePermission();
        objTypePDTO.setIdTypePermission(typeP.getIdTypePermission());
        objTypePDTO.setPermissionType(typeP.getPermissionType());
        return objTypePDTO;
    }
}
