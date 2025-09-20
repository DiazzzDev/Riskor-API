package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBBUSINESSINFO")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityBusinessInfo {
    @Id @Column(name = "IDBUSINESS", columnDefinition= "RAW(16)", insertable = false, updatable = false) //Se especifica que el ID no se ingresa manualmente o se actualiza, con su formato RAW(16)
    @Generated(event = EventType.INSERT) //Se usa la anotación Generated para mostrar que será generado el ID con SYS_GUID()
    private String idBusiness;

    @Column(name = "NAMEBUSINESS", length = 125, nullable = false, unique = true)
    private String nameBusiness;

    @Column(name = "ADDRESSBUSINESS", length = 250, nullable = false)
    private String addressBusiness;

    @Column(name = "EMAILBUSINESS", length = 256, nullable = false, unique = true)
    private String emailBusiness;

    @Column(name = "CREATIONDATE", nullable = false)
    private LocalDate creationDate;

    @Column(name = "PHONEBUSINESS", length = 15, nullable = false)
    private String phoneBusiness;

    @Column(name = "PBXBUSINESS", length = 15, nullable = false)
    private String pbxBusiness;
}
