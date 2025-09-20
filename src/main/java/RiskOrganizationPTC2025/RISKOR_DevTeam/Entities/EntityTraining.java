package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "TBTRAINING")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityTraining {
    @Id @Column(name = "IDTRAINING", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idTraining;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "FULLNAMETRAINING", length = 125, nullable = false)
    private String fullNameTraining;

    @Column(name = "REQUESTDATE", insertable = false, updatable = false)
    private LocalDate requestDate;

    @Column(name = "TRAININGDATE", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "STARTHOUR", nullable = false) @JsonFormat(pattern = "HH:mm")
    private LocalTime startHour;

    @Column(name = "ENDHOUR", nullable = false) @JsonFormat(pattern = "HH:mm")
    private LocalTime endHour;

    @Column(name = "DURATIONHOUR", nullable = false)
    private String durationHour;

    @Column(name = "TRAININGLOCATION", length = 100, nullable = false)
    private String trainingLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTRAININGMODALITY", nullable = false)
    private EntityTrainingModality idTrainingModality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}