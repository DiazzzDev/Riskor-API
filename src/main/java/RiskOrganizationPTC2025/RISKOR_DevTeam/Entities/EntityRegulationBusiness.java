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
@Table(name = "TBREGULATIONBUSINESS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityRegulationBusiness {
    @Id @Column(name = "IDREGULATION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idRegulation;

    @Column(name = "REGULATIONTITLE", nullable = false) //No se le coloca lenght porque 255 es el valor por defecto
    private String regulationTitle;

    @Column(name = "REGULATIONDESCRIPTION", length = 825, nullable = false)
    private String regulationDescription;

    @Column(name = "CREATIONDATE", nullable = false)
    private LocalDate creationDate;

    @Column(name = "REGULATIONDOCUMENT")
    private String regulationDocument;

    //Llaves foráneas (Separadas por legibilidad):

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDRISKSTATUS", nullable = false)
    private EntityRiskStatus idRiskStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDREGULATIONCATEGORY", nullable = false)
    private EntityRegulationCategory idRegulationCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDAREA", nullable = false)
    private EntityArea idArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDRISKLEVEL", nullable = false)
    private EntityRiskLevel idRiskLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
