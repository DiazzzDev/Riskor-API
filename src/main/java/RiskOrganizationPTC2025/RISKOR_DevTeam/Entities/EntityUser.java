package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "TBUSER")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityUser {
    @Id @Column(name = "USERNAME", length = 30)
    private String username;

    @Column(name = "PASSWORD", length = 256, nullable = false)
    private String password;

    @Column(name = "STATUS", insertable = false, length = 1)
    private String status;

    @Column(name = "CREATIONDATE", insertable = false, updatable = false)
    private LocalDate creationDate;
}
