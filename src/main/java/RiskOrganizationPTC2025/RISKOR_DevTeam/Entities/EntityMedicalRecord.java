package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import java.time.LocalDate;

@Entity
@Table(name = "TBMEDICALRECORD")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityMedicalRecord {
    @Id @Column(name = "IDMEDICALRECORD", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idMedicalRecord;

    @Column(name = "ALLERGIE")
    private String allergie;

    @Column(name = "CONTACTNAME")
    private String contactName;

    @Column(name = "CONTACTPHONE")
    private String contactPhone;

    @Column(name = "SPECIALNEED")
    private String specialNeed;

    @Column(name = "CREATIONDATE")
    private LocalDate creationDate;

    @Column(name = "LASTUPDATE")
    private LocalDate lastUpdate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDBLOODTYPE")
    private EntityBloodType idBloodType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDEMPLOYEE", nullable = false)
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}