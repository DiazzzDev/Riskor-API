package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTORegister {
    //Agregamos @Valid a nuestros DTOs que serán esperados en el JSON para que cumplan con sus respectivas validaciones
    @Valid
    private DTOBusinessInfo business;

    @Valid
    private DTOEmployee employee;
}
