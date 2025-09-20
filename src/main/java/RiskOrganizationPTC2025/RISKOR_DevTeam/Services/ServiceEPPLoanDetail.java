package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOEPPLoanDetail;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEPPLoanDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceEPPLoanDetail {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryEPPLoanDetail objRepoEPPLoanDetail;

    @PersistenceContext //Anotación que permite usar EntityManager
    private EntityManager em; //Invocamos a EntityManager para la persistencia de datos, haciendo referencia a businessInfo sin cargar todo desde la db

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public Page<DTOEPPLoanDetail> getAllEPPLoanDetail(String idBusiness, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityEPPLoanDetail> loanDetails = objRepoEPPLoanDetail.findByIdBusiness_IdBusiness(idBusiness.toUpperCase(), pageable);
        return loanDetails.map(this::convertTOEPPLoanDetailDTO);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOEPPLoanDetail postEPPLoanDetail(@Valid DTOEPPLoanDetail DTOEPPLoanDetail, String idBusiness){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOEPPLoanDetail == null) throw new IllegalArgumentException("No pueden haber campos vacíos");

        //Caso contrario, se procede con la inserción de datos (POST)
        EntityEPPLoanDetail objeEPPLoanDetailSaved = objRepoEPPLoanDetail.save(convertTOEPPLoanDetailEntity(DTOEPPLoanDetail, idBusiness));
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOEPPLoanDetailDTO(objeEPPLoanDetailSaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOEPPLoanDetail putEPPLoanDetail(@Valid DTOEPPLoanDetail dtoLoanDetail, String ideppLD, String idBusiness){
        if (dtoLoanDetail == null) throw new IllegalArgumentException("No pueden haber campos vacíos"); //Validamos que el DTO no venga vacío

        //Buscamos si existe el registro con el ID proporcionado
        EntityEPPLoanDetail loanDetail = objRepoEPPLoanDetail.findByIdEPPLoanDetailAndIdBusiness_IdBusiness(ideppLD, idBusiness).orElseThrow(() -> new EntityNotFoundException("Detalle del préstamo de EPP no encontrado con ID: " + ideppLD));

        //Actualizamos los campos
        loanDetail.setQuantityDelivered(dtoLoanDetail.getQuantityDelivered());
        loanDetail.setQuantityReturned(dtoLoanDetail.getQuantityReturned());

        if (dtoLoanDetail.getIdEPPInventory() != null) {
            loanDetail.setIdEPPInventory(em.getReference(EntityEPPInventory.class, dtoLoanDetail.getIdEPPInventory()));
        }

        //Retornamos el DTO actualizado
        return convertTOEPPLoanDetailDTO(loanDetail);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteEPPLoanDetail(String idEPPLoanDetail, String idBusiness){
        if (!objRepoEPPLoanDetail.existsByIdEPPLoanDetailAndIdBusiness_IdBusiness(idEPPLoanDetail, idBusiness.toUpperCase())) { return false; }

        objRepoEPPLoanDetail.deleteByIdEPPLoanDetailAndIdBusiness_IdBusiness(idEPPLoanDetail, idBusiness.toUpperCase());
        return true;
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOEPPLoanDetail convertTOEPPLoanDetailDTO(EntityEPPLoanDetail eppLoanDetail){
        DTOEPPLoanDetail objEPPLoanDetailDTO = new DTOEPPLoanDetail();
        objEPPLoanDetailDTO.setIdEPPLoanDetail(eppLoanDetail.getIdEPPLoanDetail());
        objEPPLoanDetailDTO.setQuantityDelivered(eppLoanDetail.getQuantityDelivered());
        objEPPLoanDetailDTO.setQuantityReturned(eppLoanDetail.getQuantityReturned());
        objEPPLoanDetailDTO.setIdEPPLoan(eppLoanDetail.getIdEPPLoan().getIdEPPLoan());
        objEPPLoanDetailDTO.setIdEPPInventory(eppLoanDetail.getIdEPPInventory().getIdEPPInventory());
        //Si el objeto idBusiness existe en la entidad area, obtén su ID; si no, simplemente asigna null - Esto por el uso de FETCH LAZY
        objEPPLoanDetailDTO.setIdBusiness(eppLoanDetail.getIdBusiness() != null ? eppLoanDetail.getIdBusiness().getIdBusiness() : null);

        return objEPPLoanDetailDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityEPPLoanDetail convertTOEPPLoanDetailEntity(DTOEPPLoanDetail DTOEPPLoanDetail, String idBusiness){
        EntityEPPLoanDetail objEntityEPPLoanD = new EntityEPPLoanDetail();
        objEntityEPPLoanD.setQuantityDelivered(DTOEPPLoanDetail.getQuantityDelivered());
        objEntityEPPLoanD.setQuantityReturned(DTOEPPLoanDetail.getQuantityReturned());
        objEntityEPPLoanD.setIdEPPLoan(em.getReference(EntityEPPLoan.class, DTOEPPLoanDetail.getIdEPPLoan()));
        objEntityEPPLoanD.setIdEPPInventory(em.getReference(EntityEPPInventory.class, DTOEPPLoanDetail.getIdEPPInventory()));
        objEntityEPPLoanD.setIdBusiness(em.getReference(EntityBusinessInfo.class, idBusiness.toUpperCase()));

        return objEntityEPPLoanD;
    }
}