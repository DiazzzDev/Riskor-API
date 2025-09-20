package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "TBACCIDENTNOTIFICATION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAccidentNotification {
    @Id @Column(name = "IDACCNOTIFICATION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    private String idAccNotification;

    @Column(name = "MESSAGE", insertable = false, length = 250)
    private String message;

    @Column(name = "NOTIFICATIONDATE", insertable = false)
    private LocalDate notificationDate;

    @Column(name = "EXPIRATIONDATE", insertable = false)
    private LocalDate expirationDate;

    @Column(name = "ISDELETED", insertable = false, length = 1)
    private String isDeleted;

    @Column(name = "CREATIONDATE", insertable = false)
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEMPLOYEE", insertable = false, updatable = false)
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENT", insertable = false, updatable = false)
    private EntityAccident idAccident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", insertable = false, updatable = false)
    private EntityBusinessInfo idBusiness;
}
