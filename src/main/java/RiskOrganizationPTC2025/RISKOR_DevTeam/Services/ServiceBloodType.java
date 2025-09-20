package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBloodType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBloodType;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryBloodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceBloodType {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryBloodType objRepoBT;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOBloodType> getAllBloodType(){
        List<EntityBloodType> objGetBloodT = objRepoBT.findAll();
        return objGetBloodT.stream().map(this::convertTOBloodTDTO).collect(Collectors.toList());
    }

    //Método para conversión de datos del DTO hacia la DB (método de arriba)
    public DTOBloodType convertTOBloodTDTO(EntityBloodType bloodT){
        DTOBloodType objBloodTDTO = new DTOBloodType();
        objBloodTDTO.setIdBloodType(bloodT.getIdBloodType());
        objBloodTDTO.setBloodType(bloodT.getBloodType());
        return objBloodTDTO;
    }
}
