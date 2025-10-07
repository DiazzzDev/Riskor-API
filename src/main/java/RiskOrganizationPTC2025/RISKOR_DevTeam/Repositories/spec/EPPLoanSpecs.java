package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPLoan;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public final class EPPLoanSpecs {
    private EPPLoanSpecs(){}

    // Alcance por empresa
    public static Specification<EntityEPPLoan> scope(String idBusiness) {
        return (root, query, cb) ->
                cb.equal(cb.upper(root.get("idBusiness").get("idBusiness")), idBusiness.toUpperCase());
    }

    // Rango de fechas del préstamo (opcional)
    public static Specification<EntityEPPLoan> inDateRange(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return cb.conjunction();
            if (start != null && end != null) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("loanStartDate"), start),
                        cb.lessThanOrEqualTo(root.get("loanStartDate"), end)
                );
            }
            if (start != null)  return cb.greaterThanOrEqualTo(root.get("loanStartDate"), start);
            return cb.lessThanOrEqualTo(root.get("loanStartDate"), end);
        };
    }

    // Búsqueda por nombre (first, last o "first last")
    public static Specification<EntityEPPLoan> byEmployeeName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();

            String q = name.trim().toUpperCase();
            String like = "%" + q + "%";

            Path<?> emp = root.get("idEmployee");

            Expression<String> upFirst = cb.upper(emp.get("firstName"));
            Expression<String> upLast  = cb.upper(emp.get("lastName"));
            Expression<String> full    = cb.upper(
                    cb.concat(cb.concat(emp.get("firstName"), cb.literal(" ")), emp.get("lastName"))
            );

            return cb.or(
                    cb.like(upFirst, like),
                    cb.like(upLast,  like),
                    cb.like(full,    like)
            );
        };
    }
}