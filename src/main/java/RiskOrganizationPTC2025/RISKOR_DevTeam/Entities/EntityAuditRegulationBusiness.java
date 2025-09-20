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
@Table(name = "TBAUDITREGULATIONBUSINESS")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAuditRegulationBusiness {
    @Id @Column(name = "IDAUDITRB", columnDefinition= "RAW(16)" , insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idAuditRB;

    @Column(name = "OPERATIONTYPE", insertable = false, updatable = false, length = 10)
    private String operationType;

    @Column(name = "OPERATIONDATE", insertable = false, updatable = false, nullable = false)
    private LocalDate operationDate;

    @Column(name = "USERNAME", insertable = false, updatable = false, length = 30, nullable = false)
    private String username;

    @Column(name = "IDREGULATION", insertable = false, updatable = false)
    private String idRegulation;

    @Column(name = "REGULATIONTITLE", insertable = false, updatable = false) // No se usa lenght por ser 255
    private String regulationTitle;

    @Column(name = "REGULATIONDESCRIPTION", insertable = false, updatable = false, length = 1000)
    private String regulationDescription;

    @Column(name = "CREATIONDATE", insertable = false, updatable = false)
    private LocalDate creationDate;

    @Column(name = "IDRISKSTATUS", insertable = false, updatable = false)
    private String idRiskStatus;

    @Column(name = "IDREGULATIONCATEGORY", insertable = false, updatable = false)
    private String idRegulationCategory;

    @Column(name = "IDAREA", insertable = false, updatable = false)
    private String idArea;

    @Column(name = "IDRISKLEVEL", insertable = false, updatable = false)
    private String idRiskLevel;

    @Column(name = "IDBUSINESS", insertable = false, updatable = false)
    private String idBusiness;
}
