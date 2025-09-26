package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class DTOEPPLoanSummary {
    //Integer porque int da error
    private Long quantityDelivered;
    private Long quantityReturned;
}
