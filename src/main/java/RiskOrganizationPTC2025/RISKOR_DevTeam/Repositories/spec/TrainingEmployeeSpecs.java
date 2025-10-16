package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public final class TrainingEmployeeSpecs {

    private TrainingEmployeeSpecs() {}

    /** Base: filtrar por capacitación y empresa */
    public static Specification<EntityTrainingEmployee> byTrainingAndBusiness(String idTraining, String idBusiness) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("idTraining").get("idTraining"), idTraining),
                cb.equal(cb.upper(root.get("idBusiness").get("idBusiness")), idBusiness.toUpperCase())
        );
    }

    /** Búsqueda por nombre, DUI o email del empleado asociado */
    public static Specification<EntityTrainingEmployee> searchEmployee(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();

            String uq = q.trim().toUpperCase();

            Join<EntityTrainingEmployee, EntityEmployee> e = root.join("idEmployee", JoinType.INNER);

            Expression<String> fullName = cb.upper(cb.concat(cb.concat(e.get("firstName"), " "), e.get("lastName")));
            Expression<String> duiNorm = cb.function("replace", String.class, cb.upper(e.get("dui")), cb.literal("-"), cb.literal(""));

            String uqDuiNoDash = uq.replace("-", "");

            Predicate byFullName = cb.like(fullName, "%" + uq + "%");
            Predicate byFirst    = cb.like(cb.upper(e.get("firstName")), "%" + uq + "%");
            Predicate byLast     = cb.like(cb.upper(e.get("lastName")), "%" + uq + "%");
            Predicate byEmail    = cb.like(cb.upper(e.get("employeeEmail")), "%" + uq + "%");
            Predicate byDui      = cb.like(duiNorm, "%" + uqDuiNoDash + "%");

            return cb.or(byFullName, byFirst, byLast, byEmail, byDui);
        };
    }
}