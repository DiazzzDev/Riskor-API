package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBBLOODTYPE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityBloodType {
    @Id @Column(name = "IDBLOODTYPE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idBloodType;
    @Column(name = "BLOODTYPE", length = 3, nullable = false, updatable = false)
    private String bloodType;
}
