package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBCONTROLSDSTATUS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityControlSDStatus {
    @Id @Column(name = "IDCONTROLSDSTATUS", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idControlSDStatus;
    @Column(name = "NAMECONTROLSTATUS", length = 50, nullable = false, updatable = false)
    private String nameControlStatus;
}
