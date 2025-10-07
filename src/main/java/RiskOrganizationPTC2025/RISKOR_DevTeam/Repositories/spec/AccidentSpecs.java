package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityAccident;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AccidentSpecs {
    private AccidentSpecs() {}

    // Alcance por empresa
    public static Specification<EntityAccident> scope(String idBusiness) {
        return (root, query, cb) ->
                cb.equal(cb.upper(root.get("idBusiness").get("idBusiness")), idBusiness.toUpperCase());
    }

    // Filtro por empleado exacto (opcional)
    public static Specification<EntityAccident> byEmployeeId(String employeeId) {
        return (root, query, cb) -> {
            if (employeeId == null || employeeId.isBlank()) return cb.conjunction();
            return cb.equal(root.get("idEmployee").get("idEmployee"), employeeId);
        };
    }

    // Filtro por estatus de accidente (opcional)
    public static Specification<EntityAccident> byStatus(String statusId) {
        return (root, query, cb) -> {
            if (statusId == null || statusId.isBlank()) return cb.conjunction();
            return cb.equal(root.get("idAccidentStatus").get("idAccidentStatus"), statusId);
        };
    }

    // Rango de fechas (opcional)
    public static Specification<EntityAccident> inDateRange(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("accidentDate"), from),
                        cb.lessThanOrEqualTo(root.get("accidentDate"), to)
                );
            }
            if (from != null) return cb.greaterThanOrEqualTo(root.get("accidentDate"), from);
            return cb.lessThanOrEqualTo(root.get("accidentDate"), to);
        };
    }

    // Búsqueda por nombre, DUI (con/sin guión) o correo (opcional)
    public static Specification<EntityAccident> searchQ(String employeeInfo) {
        return (root, query, cb) -> {
            if (employeeInfo == null || employeeInfo.isBlank()) return cb.conjunction();

            String q = employeeInfo.trim().toUpperCase();
            String like = "%" + q + "%";
            String qNoDash = q.replace("-", "");
            String likeNoDash = "%" + qNoDash + "%";

            // join a empleado
            Path<?> emp = root.get("idEmployee");

            // Campos
            Expression<String> email    = cb.upper(emp.get("employeeEmail"));
            Expression<String> first    = cb.upper(emp.get("firstName"));
            Expression<String> last     = cb.upper(emp.get("lastName"));
            Expression<String> fullName = cb.upper(
                    cb.concat(cb.concat(emp.get("firstName"), " "), emp.get("lastName"))
            );

            // DUI sin guiones: especifica tipo String en la function para que NO sea Object
            Expression<String> duiNoDash =
                    cb.function("REPLACE", String.class, cb.upper(emp.get("dui")),
                            cb.literal("-"), cb.literal(""));

            return cb.or(
                    cb.like(email,    like),
                    cb.like(first,    like),
                    cb.like(last,     like),
                    cb.like(fullName, like),
                    cb.like(duiNoDash, likeNoDash)
            );
        };
    }
}
