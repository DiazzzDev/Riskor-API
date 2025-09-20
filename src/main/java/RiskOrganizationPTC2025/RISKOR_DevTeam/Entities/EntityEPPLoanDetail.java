package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBEPPLOANDETAIL")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityEPPLoanDetail {
    @Id @Column(name = "IDEPPLOANDETAIL", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idEPPLoanDetail;

    @Column(name = "QUANTITYDELIVERED")
    private int quantityDelivered;

    @Column(name = "QUANTITYRETURNED")
    private int quantityReturned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEPPLOAN")
    private EntityEPPLoan idEPPLoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEPPINVENTORY")
    private EntityEPPInventory idEPPInventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
