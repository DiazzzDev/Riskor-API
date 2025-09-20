package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBMAINTENANCECONTROLSD")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityMaintenanceControlSD {
    @Id @Column(name = "IDMAINTENANCECONTROLSD", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idMaintenanceControlSD;

    @Column(name = "DATEMAINTENANCE")
    private LocalDate dateMaintenance;

    @Column(name = "DESCRIPTION", length = 250)
    private String description;

    @Column(name = "CARRIEDOUTBY", length = 125)
    private String carriedOutBy;

    @Column(name = "OBSERVATION", length = 250)
    private String observation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDSERVICEDEVICESSO")
    private EntityControlSDSSO idServiceDeviceSSO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
