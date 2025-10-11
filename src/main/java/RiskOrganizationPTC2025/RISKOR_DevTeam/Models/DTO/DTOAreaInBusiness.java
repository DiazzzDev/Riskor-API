package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//JSON Personalizado para respuesta
@Getter @Setter
public class DTOAreaInBusiness {
    private DTOArea area;
    private List<DTOLocation> locationsInArea;
    private List<DTOEmployee> employeesOnArea;
}
