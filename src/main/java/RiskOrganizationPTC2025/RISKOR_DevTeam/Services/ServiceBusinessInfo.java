package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryBusinessInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceBusinessInfo {
    @Autowired
    private RepositoryBusinessInfo objRepoBI;

    @Transactional(readOnly = true)
    public DTOBusinessInfo getBusinessById(String idBusiness) {
        EntityBusinessInfo information = objRepoBI.findById(idBusiness).orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));
        return convertToDTO(information);
    }

    public DTOBusinessInfo insertBusinessInfo(@Valid DTOBusinessInfo dtoBI){ //El método pide el DTO para saber que va a enviar a la entidad
        if(dtoBI == null) throw new IllegalArgumentException("No pueden haber campos vacios");

        //Crea un objEntidad donde se convierte el argumento (JSON) a entidad
        EntityBusinessInfo saved = objRepoBI.save(convertToEntity(dtoBI)); //Se crea otro objEntidad donde enviará objEntidad anterior a la db con el Repository
        //save() guarda la entidad en la db, usado para la persistencia de datos
        return convertToDTO(saved);
    }

    public DTOBusinessInfo putBusinessInfo(@Valid DTOBusinessInfo dtoBI, String idBusiness){
        if(dtoBI == null){
            throw new IllegalArgumentException("No pueden haber campos vacios");
        }
        EntityBusinessInfo businessInfoExists = objRepoBI.findById(idBusiness).orElseThrow(() -> new EntityNotFoundException("Negocio no encontrado con ID: " + idBusiness));

        businessInfoExists.setNameBusiness(dtoBI.getNameBusiness());
        businessInfoExists.setAddressBusiness(dtoBI.getAddressBusiness());
        businessInfoExists.setEmailBusiness(dtoBI.getEmailBusiness());
        businessInfoExists.setCreationDate(dtoBI.getCreationDate());
        businessInfoExists.setPhoneBusiness(dtoBI.getPhoneBusiness());
        businessInfoExists.setPbxBusiness(dtoBI.getPbxBusiness());

        EntityBusinessInfo businessInfo = objRepoBI.save(businessInfoExists);
        return convertToDTO(businessInfo);
    }

    public boolean removeBusinessInfo(String idBusiness) {
        try{
            if (idBusiness == null || idBusiness.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID del negocio no puede ser nulo o vacío");
            }

            boolean exists = objRepoBI.existsById(idBusiness);
            if (!exists) {
                throw new EntityNotFoundException("No se encontró el negocio con ID: " + idBusiness);
            }

            objRepoBI.deleteById(idBusiness);
            return true;
        }
        catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("No se encontró el negocio");
        }
    }

    private DTOBusinessInfo convertToDTO(EntityBusinessInfo businessInfo) {
        DTOBusinessInfo dtoBI = new DTOBusinessInfo();
        dtoBI.setIdBusiness(businessInfo.getIdBusiness());
        dtoBI.setNameBusiness(businessInfo.getNameBusiness());
        dtoBI.setAddressBusiness(businessInfo.getAddressBusiness());
        dtoBI.setEmailBusiness(businessInfo.getEmailBusiness());
        dtoBI.setCreationDate(businessInfo.getCreationDate());
        dtoBI.setPhoneBusiness(businessInfo.getPhoneBusiness());
        dtoBI.setPbxBusiness(businessInfo.getPbxBusiness());
        return dtoBI;
    }

    private EntityBusinessInfo convertToEntity(DTOBusinessInfo dtoBI) {
        EntityBusinessInfo businessInfo = new EntityBusinessInfo();
        businessInfo.setNameBusiness(dtoBI.getNameBusiness());
        businessInfo.setAddressBusiness(dtoBI.getAddressBusiness());
        businessInfo.setEmailBusiness(dtoBI.getEmailBusiness());
        businessInfo.setCreationDate(dtoBI.getCreationDate());
        businessInfo.setPhoneBusiness(dtoBI.getPhoneBusiness());
        businessInfo.setPbxBusiness(dtoBI.getPbxBusiness());
        return businessInfo;
    }
}
