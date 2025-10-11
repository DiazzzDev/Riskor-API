package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataDuplicate;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOBusinessInfo;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORegister;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryBusinessInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceBusinessInfo {
    @Autowired
    private RepositoryBusinessInfo objRepoBI;

    @Autowired
    private ServiceEmployee objServiceE;

    @Transactional(readOnly = true)
    public DTOBusinessInfo getBusinessById(String idBusiness) {
        EntityBusinessInfo information = objRepoBI.findById(idBusiness).orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));
        return convertToDTO(information);
    }

    //Como trabaja con varias tablas, si en una algo sale mal elimina lo realizado para evtar registros flotantes
    @Transactional(rollbackFor = Exception.class)
    public DTORegister postRegister(@Valid DTORegister dto){
        //Creamos un dto de Empresa donde guardamos el que contiene el DTORegister, para utilizar insertBusinessInfo() en el registro de la empresa
        DTOBusinessInfo dtoBusinessInfo = dto.getBusiness();
        dto.setBusiness(insertBusinessInfo(dtoBusinessInfo)); //Mandamos a llamar el método que insertará la empresa

        //Repetimos proceso con el empleado, obtenemos el DTOEmployee que contiene el DTORegister
        DTOEmployee dtoEmployee = dto.getEmployee(); //Mandamos a llamar el método para post de empleado
        dto.setEmployee(objServiceE.postEmployee(dtoEmployee, dto.getBusiness().getIdBusiness(), null, true)); //Asignamos TRUE para que sea administrador al momento de crear el usuario

        return dto; //Se devolverá el primer usuario y empresa registrados
    }

    @Transactional(rollbackFor = Exception.class)
    public DTOBusinessInfo insertBusinessInfo(@Valid DTOBusinessInfo dtoBI){ //El método pide el DTO para saber que va a enviar a la entidad
        if(dtoBI == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        if (objRepoBI.existsByNameBusinessIgnoreCase(dtoBI.getNameBusiness()))
            throw new ExceptionDataDuplicate("Datos duplicados en Nombre de la empresa");
        if (objRepoBI.existsByEmailBusinessIgnoreCase(dtoBI.getEmailBusiness()))
            throw new ExceptionDataDuplicate("Datos duplicados en Correo");

        //Crea un objEntidad donde se convierte el argumento (JSON) a entidad
        EntityBusinessInfo saved = objRepoBI.save(convertToEntity(dtoBI)); //Se crea otro objEntidad donde enviará objEntidad anterior a la db con el Repository
        //save() guarda la entidad en la db, usado para la persistencia de datos
        return convertToDTO(saved);
    }

    public DTOBusinessInfo putBusinessInfo(@Valid DTOBusinessInfo dtoBI, String idBusiness){
        if(dtoBI == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        EntityBusinessInfo businessInfo = objRepoBI.findById(idBusiness).orElseThrow(() -> new EntityNotFoundException("Negocio no encontrado con ID: " + idBusiness));

        businessInfo.setNameBusiness(dtoBI.getNameBusiness());
        businessInfo.setAddressBusiness(dtoBI.getAddressBusiness());
        businessInfo.setEmailBusiness(dtoBI.getEmailBusiness());
        businessInfo.setCreationDate(dtoBI.getCreationDate());
        businessInfo.setPhoneBusiness(dtoBI.getPhoneBusiness());
        businessInfo.setPbxBusiness(dtoBI.getPbxBusiness());
        businessInfo.setNIT(dtoBI.getNIT());

        return convertToDTO(businessInfo);
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
        dtoBI.setNIT(businessInfo.getNIT());

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
        businessInfo.setNIT(dtoBI.getNIT());

        return businessInfo;
    }
}
