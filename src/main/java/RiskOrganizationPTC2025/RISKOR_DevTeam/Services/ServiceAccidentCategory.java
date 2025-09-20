package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccidentCategory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOAccidentCategory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryAccidentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceAccidentCategory {
    @Autowired
    private RepositoryAccidentCategory objRepoAC;

    @Transactional(readOnly = true)
    public List<DTOAccidentCategory> getAllAccidentCategories(){
        List<EntityAccidentCategory> categories = objRepoAC.findAll();
        return categories.stream().map(this::convertToAccidentCategoryDTO).collect(Collectors.toList());
    }

    public DTOAccidentCategory convertToAccidentCategoryDTO(EntityAccidentCategory accidentCategory){
        DTOAccidentCategory accidentCategoryDTO = new DTOAccidentCategory();
        accidentCategoryDTO.setIdAccidentCategory(accidentCategory.getIdAccidentCategory());
        accidentCategoryDTO.setAccidentCategory(accidentCategory.getAccidentCategory());
        return accidentCategoryDTO;
    }
}
