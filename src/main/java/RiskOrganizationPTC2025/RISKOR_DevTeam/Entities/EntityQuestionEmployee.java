package RiskOrganizationPTC2025.RISKOR_DevTeam.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "TBQUESTIONEMPLOYEE")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityQuestionEmployee {

    @Id @Column(name = "IDQUESTIONSEMPLOYEE", columnDefinition = "RAW(16)", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private String idQuestionsEmployee;
    @Column(name = "ANSWEREMPLOYEE")
    private String answerEmployee;
    @ManyToOne @JoinColumn(name = "IDEMPLOYEE")
    private EntityEmployee idEmployee;
    @ManyToOne @JoinColumn(name = "IDQUESTION")
    private EntitySecurityQuestion idQuestion;
}
