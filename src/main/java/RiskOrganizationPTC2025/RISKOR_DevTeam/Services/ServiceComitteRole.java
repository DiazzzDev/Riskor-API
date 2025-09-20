package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityComitteRole;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOComitteRole;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryComitteRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceComitteRole {
    @Autowired
    private RepositoryComitteRole objRepoCommitteR;

    @Transactional(readOnly = true)
    public List<DTOComitteRole> getAllComitteRoles(){
        List<EntityComitteRole> committeRoles = objRepoCommitteR.findAll();
        return committeRoles.stream().map(this::convertToComitteDTO).collect(Collectors.toList());
    }

    public DTOComitteRole convertToComitteDTO(EntityComitteRole role){
        DTOComitteRole objDTOCommitte = new DTOComitteRole();
        objDTOCommitte.setIdComitteR(role.getIdRole());
        objDTOCommitte.setCommitteRoleName(role.getCommitteRoleName());
        return objDTOCommitte;
    }
}
