package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDate;

@Entity
@Table(name = "TBAUDITACCIDENTREGULATION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAuditAccidentRegulation {

    @Id @Column(name = "IDAUDIT", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private String idAudit;
    @Column(name = "OPERATIONTYPE")
    private String operationType;
    @Column(name = "OPERATIONDATE", insertable = false)
    private LocalDate operationDate;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "IDACCIDENTREGULATION")
    private String idAccidentRegulation;
    @Column(name = "IDACCIDENT")
    private String idAccident;
    @Column(name = "IDREGULATION")
    private String idRegulation;
}
