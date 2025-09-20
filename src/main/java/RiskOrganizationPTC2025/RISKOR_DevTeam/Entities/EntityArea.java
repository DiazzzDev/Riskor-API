package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "TBAREA")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityArea {
    @Id @Column(name = "IDAREA", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idArea;

    @Column(name = "AREANAME", length = 75, nullable = false)
    private String areaName;

    @Column(name = "AREASKETCH", length = 1000, nullable = false)
    private String areaSketch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", nullable = false)
    private EntityBusinessInfo idBusiness;
}
