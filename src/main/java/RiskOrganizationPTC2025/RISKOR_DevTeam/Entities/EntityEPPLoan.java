package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBEPPLOAN")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityEPPLoan {
    @Id @Column(name = "IDEPPLOAN", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idEPPLoan;

    @Column(name = "LOANSTARTDATE", insertable = false)
    private LocalDate loanStartDate;

    @Column(name = "LOANRETURNDATE")
    private LocalDate loanReturnDate;

    @Column(name = "QUANTITYDELIVERED")
    private int quantityDelivered;

    @Column(name = "QUANTITYRETURNED")
    private int quantityReturned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEPPINVENTORY")
    private EntityEPPInventory idEPPInventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEMPLOYEE")
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
