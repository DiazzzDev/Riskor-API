package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBMEDICALHISTORY")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityMedicalHistory {
    @Id @Column(name = "IDMEDICALHISTORY", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idMedicalHistory;

    @Column(name = "MEDICALCONDITION")
    private String medicalCondition;

    @Column(name = "DIAGNOSISDATE")
    private LocalDate diagnosisDate;

    @Column(name = "TREATMENT")
    private String treatment;

    @Column(name = "TREATMENTSTARTDATE")
    private LocalDate treatmentStartDate;

    @Column(name = "TREATMENTENDDATE")
    private LocalDate treatmentEndDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDMEDICALSTATUS", nullable = false)
    private EntityMedicalStatus idMedicalStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDMEDICALRECORD", nullable = false)
    private EntityMedicalRecord idMedicalRecord;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}