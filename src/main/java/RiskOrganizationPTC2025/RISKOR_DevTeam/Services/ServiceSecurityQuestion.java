package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntitySecurityQuestion;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOSecurityQuestion;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositorySecurityQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceSecurityQuestion {
    //Inyectamos el repositorio
    @Autowired
    private RepositorySecurityQuestion objRepoSQ;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOSecurityQuestion> getAllSecurityQ(){
        List<EntitySecurityQuestion> objGetSecurityQ = objRepoSQ.findAll();
        return objGetSecurityQ.stream().map(this::convertTOSecurityQDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOSecurityQuestion convertTOSecurityQDTO(EntitySecurityQuestion securityQ){
        DTOSecurityQuestion objSecurityQDTO = new DTOSecurityQuestion();
        objSecurityQDTO.setIdQuestion(securityQ.getIdQuestion());
        objSecurityQDTO.setNameQuestion(securityQ.getNameQuestion());
        return objSecurityQDTO;
    }
}
