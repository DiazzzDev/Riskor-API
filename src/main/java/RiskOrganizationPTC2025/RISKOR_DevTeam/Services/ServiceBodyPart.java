package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBodyPart;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryBodyPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceBodyPart {
    @Autowired
    private RepositoryBodyPart objRepoBP;

    @Transactional(readOnly = true)
    public List<DTOBodyPart> getAllBodyParts(){
        List<EntityBodyPart> bodyParts = objRepoBP.findAll();
        return bodyParts.stream().map(this::convertToBodyPartDTO).collect(Collectors.toList());
    }

    public DTOBodyPart convertToBodyPartDTO(EntityBodyPart part){
        DTOBodyPart objDTOBP = new DTOBodyPart();
        objDTOBP.setIdBodyPart(part.getIdBodyPart());
        objDTOBP.setBodyPart(part.getBodyPart());
        return objDTOBP;
    }
}
