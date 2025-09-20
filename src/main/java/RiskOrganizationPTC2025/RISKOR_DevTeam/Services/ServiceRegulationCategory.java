package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRegulationCategory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTORegulationCategory;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryRegulationCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRegulationCategory {
    @Autowired
    private RepositoryRegulationCategory objRepoRC;

    @Transactional(readOnly = true)
    public List<DTORegulationCategory> getAllRegulationCategories(){
        List<EntityRegulationCategory> categories = objRepoRC.findAll();
        return categories.stream().map(this::convertToRegulationCategoryDTO).collect(Collectors.toList());
    }

    public DTORegulationCategory convertToRegulationCategoryDTO(EntityRegulationCategory category){
        DTORegulationCategory objDTORC = new DTORegulationCategory();
        objDTORC.setIdRegulationCategory(category.getIdRegulationCategory());
        objDTORC.setRegulationCategory(category.getRegulationCategory());
        return objDTORC;
    }
}
