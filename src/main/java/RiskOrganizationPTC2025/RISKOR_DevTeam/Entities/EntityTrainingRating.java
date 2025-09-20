package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBTRAININGRATING")
@Getter @Setter
@ToString(exclude = {"idTrainingEmployee","idBusiness"})
@EqualsAndHashCode
public class EntityTrainingRating {
    @Id @Column(name = "IDTRAININGRATING", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idTrainingRating;

    @Column(name = "RATINGTRAINING", nullable = false)
    private Integer ratingTraining;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDTRAININGEMPLOYEE", nullable = false)
    private EntityTrainingEmployee idTrainingEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
