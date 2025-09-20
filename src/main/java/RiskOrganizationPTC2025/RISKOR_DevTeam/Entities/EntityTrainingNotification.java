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
@Table(name = "TBTRAININGNOTIFICATION")
@Getter @Setter
@ToString(exclude = {"idEmployee","idTraining","idBusiness"}) //Evitamos RECARGAR elementos que se mandan a llamar a si mismos (problema STACK OVERFLOW)
@EqualsAndHashCode
public class EntityTrainingNotification {
    @Id @Column(name = "IDTRNNOTIFICATION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idTrnNotification;

    @Column(name = "MESSAGE", length = 250, nullable = false)
    private String message;

    @Column(name = "NOTIFICATIONDATE", nullable = false)
    private LocalDate notificationDate;

    @Column(name = "EXPIRATIONDATE", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "ISDELETED", length = 1, nullable = false)
    private String isDeleted; // 'Y'|'N'

    @Column(name = "CREATIONDATE", nullable = false)
    private LocalDate creationDate;

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
