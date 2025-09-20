package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityComittePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOComittePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryComittePosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceComittePosition {
    @Autowired
    private RepositoryComittePosition objRepoCommitteP;

    @Transactional(readOnly = true)
    public List<DTOComittePosition> getAllComitteRoles(){
        List<EntityComittePosition> objRoles = objRepoCommitteP.findAll();
        return objRoles.stream().map(this::convertToComittePDTO).collect(Collectors.toList());
    }

    public DTOComittePosition convertToComittePDTO(EntityComittePosition position){
        DTOComittePosition objPosistions = new DTOComittePosition();
        objPosistions.setIdComitteP(position.getIdComitteP());
        objPosistions.setComittePositionName(position.getCommittePositionName());
        return objPosistions;
    }
}
