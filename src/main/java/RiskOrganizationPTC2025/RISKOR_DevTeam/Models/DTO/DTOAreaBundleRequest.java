package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//Json personalizado para guardar
@Getter @Setter
public class DTOAreaBundleRequest {
    @Valid @NotNull
    private DTOArea area;

    @Valid
    private List<DTOLocationCreate> locations;

    private List<String> employeeIds;
}
