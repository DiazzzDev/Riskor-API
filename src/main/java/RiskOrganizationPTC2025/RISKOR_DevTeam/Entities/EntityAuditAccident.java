package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "TBAUDITACCIDENT")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAuditAccident {
    @Id @Column(name = "IDAUDITACCIDENT", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idAuditAccident;

    @Column(name = "OPERATIONTYPE", insertable = false, updatable = false, length = 10)
    private String operationType;

    @Column(name = "OPERATIONDATE", insertable = false, updatable = false)
    private LocalDate operationDate;

    @Column(name = "USERNAME", insertable = false, updatable = false, length = 50)
    private String username;

    @Column(name = "IDACCIDENT", insertable = false, updatable = false)
    private String idAccident;

    @Column(name = "DESCRIPTION", insertable = false, updatable = false, length = 250)
    private String description;

    @Column(name = "ACCIDENTDATE", insertable = false, updatable = false)
    private LocalDate accidentDate;

    @Column(name = "REPORTACCIDENT", insertable = false, updatable = false)
    private LocalDate reportAccident;

    @Column(name = "IDACCIDENTCATEGORY", insertable = false, updatable = false)
    private String idAccidentCategory;

    @Column(name = "IDACCIDENTTYPE", insertable = false, updatable = false)
    private String idAccidentType;

    @Column(name = "IDACCIDENTSEVERITY", insertable = false, updatable = false)
    private String idAccidentSeverity;

    @Column(name = "IDACCIDENTSTATUS", insertable = false, updatable = false)
    private String idAccidentStatus;

    @Column(name = "IDEMPLOYEE", insertable = false, updatable = false)
    private String idEmployee;

    @Column(name = "IDLOCATION", insertable = false, updatable = false)
    private String idLocation;

    @Column(name = "IDBUSINESS", insertable = false, updatable = false)
    private String idBusiness;
}
