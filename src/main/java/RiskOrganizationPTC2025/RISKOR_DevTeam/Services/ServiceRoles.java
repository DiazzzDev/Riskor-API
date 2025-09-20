package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRoles;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORoles;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRoles {
    @Autowired
    private RepositoryRoles objRepoR;

    //Método para obtener los roles en lista
    @Transactional(readOnly = true)
    public List<DTORoles> getAllRoles(){
        List<EntityRoles> objRoles = objRepoR.findAll();
        return objRoles.stream().map(this::convertToRolesDTO).collect(Collectors.toList());
    }

    public DTORoles convertToRolesDTO(EntityRoles roles){
        DTORoles objDTORoles = new DTORoles();
        objDTORoles.setIdRole(roles.getIdRole());
        objDTORoles.setRoleName(roles.getRoleName());
        return objDTORoles;
    }
}
