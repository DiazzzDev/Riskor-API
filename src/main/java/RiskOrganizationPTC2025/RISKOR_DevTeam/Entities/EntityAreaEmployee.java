package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBAREAEMPLOYEE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityAreaEmployee {
    @Id @Column(name = "IDAREAEMPLOYEE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idAreaEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDAREA", nullable = false)
    private EntityArea idArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEMPLOYEE", nullable = false)
    private EntityEmployee idEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
