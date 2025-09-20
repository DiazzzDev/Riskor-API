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
@Table(name = "TBTRAININGEMPLOYEE")
@Getter @Setter
@ToString(exclude = {"idEmployee","idTraining","idBusiness"}) //Prevenimos error STACK OVERFLOW, el cual sucede cuando 2 entidades (o más) mandan a llamarse a sí mismas
@EqualsAndHashCode
public class EntityTrainingEmployee {
    @Id @Column(name = "IDTRAININGEMPLOYEE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idTrainingEmployee;

    @Column(name = "ATTENDANCE", length = 1)
    private String attendance;  //Solo puede ser 'S' | 'N' | NULL

    @Column(name = "ATTENDANCEDATE", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "OBSERVATION")
    private String observation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDEMPLOYEE", nullable = false)
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDTRAINING", nullable = false)
    private EntityTraining idTraining;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
