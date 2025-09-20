package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBACCIDENTBODYPART")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAccidentBodyPart {
    @Id @Column(name = "IDACCIDENTBODYPART", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idAccidentBodyPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDACCIDENT", nullable = false)
    private EntityAccident idAccident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBODYPART", nullable = false)
    private EntityBodyPart idBodyPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
