package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOCloudinary {

    public DTOCloudinary(String url, String publicId){
        this.url = url;
        this.publicId = publicId;
    }

    private String url;
    private String publicId;
}
