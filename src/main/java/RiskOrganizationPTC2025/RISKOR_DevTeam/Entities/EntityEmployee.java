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
@Table(name = "TBEMPLOYEE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityEmployee {
    @Id @Column(name = "IDEMPLOYEE", columnDefinition= "RAW(16)", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String idEmployee;

    @Column(name = "FIRSTNAME", length = 75, nullable = false)
    private String firstName;

    @Column(name = "LASTNAME", length = 75, nullable = false)
    private String lastName;

    @Column(name = "GENDER", length = 1, nullable = false)
    private String gender;

    @Column(name = "AGE", nullable = false)
    private Integer age;

    @Column(name = "BIRTHDATE", nullable = false)
    private LocalDate birthdate;

    @Column(name = "DUI", length = 10, nullable = false, unique = true)
    private String dui;

    @Column(name = "AFFILIATIONISSS", nullable = false)
    private Double affiliationISSS;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "PERSONALPHONE", length = 15, nullable = false)
    private String personalPhone;

    @Column(name = "PHOTO", length = 1000, nullable = false)
    private String photo;

    @Column(name = "EMPLOYEEMAIL", length = 125, nullable = false)
    private String employeeEmail;

    @Column(name = "STARTDATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "ENDDATE")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDROLE", nullable = false)
    private EntityRoles idRole;

    @OneToOne
    @JoinColumn(name = "USERNAME", nullable = false, unique = true)
    private EntityUser username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCOMITTEPOSITION")
    private EntityComittePosition idCommitteePosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCOMITTEROLE")
    private EntityComitteRole idCommitteeRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDEMPLOYEEPOSITION", nullable = false)
    private EntityEmployeePosition idEmployeePosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDBUSINESS", updatable = false, nullable = false)
    private EntityBusinessInfo idBusiness;
}
