package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import java.time.LocalDate;

@Entity
@Table(name = "TBACCIDENT")
@Getter @Setter
@ToString(exclude = {"idAccidentCategory","idAccidentType","idAccidentSeverity","idAccidentStatus","idEmployee","idLocation","idBusiness"})
@EqualsAndHashCode
public class EntityAccident {
    @Id @Column(name = "IDACCIDENT", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idAccident;

    @Column(name = "DESCRIPTION", length = 250, nullable = false)
    private String description;

    @Column(name = "ACCIDENTDATE", nullable = false)
    private LocalDate accidentDate;

    @Column(name = "REPORTACCIDENT", nullable = false)
    private LocalDate reportAccident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENTCATEGORY")
    private EntityAccidentCategory idAccidentCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENTTYPE")
    private EntityAccidentType idAccidentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENTSEVERITY")
    private EntityAccidentSeverity idAccidentSeverity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENTSTATUS")
    private EntityAccidentStatus idAccidentStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDEMPLOYEE", nullable = false)
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDLOCATION", nullable = false)
    private EntityLocation idLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENTBY", nullable = false)
    private EntityEmployee sentBy;
}
