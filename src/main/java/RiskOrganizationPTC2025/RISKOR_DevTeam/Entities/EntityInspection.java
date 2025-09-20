package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBINSPECTION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityInspection {
    @Id @Column(name = "IDINSPECTION", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idInspection;

    @Column(name = "INSPECTIONDATE", insertable = false, updatable = false)
    private LocalDate inspectionDate;

    @Column(name = "OBSERVATION")
    private String observation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEMPLOYEE")
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDAREA")
    private EntityArea idArea;

    @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "IDINSPECTIONTYPE")
    private EntityInspectionType idInspectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDINSPECTIONSTATUS")
    private EntityInspectionStatus idInspectionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
