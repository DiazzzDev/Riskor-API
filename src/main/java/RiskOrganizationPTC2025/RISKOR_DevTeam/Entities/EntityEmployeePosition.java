package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBEMPLOYEEPOSITION")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityEmployeePosition {
    @Id @Column(name = "IDEMPLOYEEPOSITION", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idEmployeePosition;

    @Column(name = "EMPLOYEEPOSITION", length = 125, nullable = false)
    private String employeePosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
