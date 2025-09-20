package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBCONTROLSDSSO")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityControlSDSSO {
    @Id @Column(name = "IDSERVICEDEVICESSO", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idServiceDeviceSSO;

    @Column(name = "NAMESERVICEDEVICE")
    private String nameServiceDevice;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "INSTALLATIONDATE")
    private LocalDate installationDate;

    @Column(name = "MAINTENANCEDATE")
    private LocalDate maintenanceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEMPLOYEE")
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTYPECONTROLSD")
    private EntityTypeControlSafetyDevice idTypeControlSD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDLOCATION")
    private EntityLocation idLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCONTROLSDSTATUS")
    private EntityControlSDStatus idControlSDStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
