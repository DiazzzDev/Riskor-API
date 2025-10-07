package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployeePosition;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityRoles;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityTrainingEmployee;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

/**
 * Esta clase es especial, está colocada aquí porque hace referencia a la capa de recolección de datos
 * Los specs o Specifications sirven para realizar consultas DINÁMICAS (CLAVE para los filtros)
 * Esto para facilitar filtros de múltiples parámetros y que con una consulta Query no son ni óptimos ni escalables como aquí
 */
public final class EmployeeSpecs {

    private EmployeeSpecs() {}

    /** Base: por empresa, activos y que NO pertenezcan al comité (si falta cualquiera de las 2 FKs). */
    public static Specification<EntityEmployee> base(String idBusiness) {
        return (root, query, cb) -> {
            // join a usuario para filtrar status
            var u = root.join("username"); // EntityUser

            Predicate byBusiness = cb.equal(
                    cb.upper(root.get("idBusiness").get("idBusiness")),
                    idBusiness.toUpperCase()
            );

            // No pertenece al comité si falta posición o rol del comité
            Predicate notInCommittee = cb.or(
                    cb.isNull(root.get("idCommitteePosition")),
                    cb.isNull(root.get("idCommitteeRole"))
            );

            Predicate active = cb.equal(u.get("status"), "T");

            return cb.and(byBusiness, notInCommittee, active);
        };
    }

    /** Búsqueda libre por nombre/apellido, DUI (con/sin guión) o email del empleado. */
    public static Specification<EntityEmployee> searchQ(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();

            String uq = q.trim().toUpperCase();

            // Nombre completo: CONCAT de 2 en 2
            Expression<String> fullName =
                    cb.upper(cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName")));

            // DUI normalizado (sin guiones) usando función tipada
            Expression<String> duiNorm =
                    cb.function("replace", String.class, cb.upper(root.get("dui")), cb.literal("-"), cb.literal(""));
            String uqDuiNoDash = uq.replace("-", "");

            Predicate byFullName = cb.like(fullName, "%" + uq + "%");
            Predicate byFirst    = cb.like(cb.upper(root.get("firstName")), "%" + uq + "%");
            Predicate byLast     = cb.like(cb.upper(root.get("lastName")),  "%" + uq + "%");
            Predicate byEmail    = cb.like(cb.upper(root.get("employeeEmail")), "%" + uq + "%");
            Predicate byDui      = cb.like(duiNorm, "%" + uqDuiNoDash + "%");

            return cb.or(byFullName, byFirst, byLast, byEmail, byDui);
        };
    }

    /** Filtro por rol (tabla TBROLE -> EntityRoles.roleName). */
    public static Specification<EntityEmployee> byRole(String roleName) {
        return (root, query, cb) -> {
            if (roleName == null || roleName.isBlank()) return cb.conjunction();
            Join<EntityEmployee, EntityRoles> r = root.join("idRole"); // many-to-one
            return cb.equal(cb.upper(r.get("roleName")), roleName.trim().toUpperCase());
        };
    }

    /** Filtro por cargo profesional: por ID (hex de RAW(16)) o por nombre (employeePosition). */
    public static Specification<EntityEmployee> byPosition(String idOrName) {
        return (root, query, cb) -> {
            if (idOrName == null || idOrName.isBlank()) return cb.conjunction();
            Join<EntityEmployee, EntityEmployeePosition> p =
                    root.join("idEmployeePosition", JoinType.LEFT); // por si hubiera nulos

            String v = idOrName.trim().toUpperCase();
            Predicate byId   = cb.equal(cb.upper(p.get("idEmployeePosition")), v);
            Predicate byName = cb.equal(cb.upper(p.get("employeePosition")), v);
            return cb.or(byId, byName);
        };
    }

    /** Filtro por empresa */
    public static Specification<EntityEmployee> byBusiness(String idBusiness) {
        return (root, query, cb) ->
                cb.equal(cb.upper(root.get("idBusiness").get("idBusiness")), idBusiness.toUpperCase());
    }

    /** Empleados de una empresa con un status específico (T=activo, F=inactivo). */
    public static Specification<EntityEmployee> byBusinessAndStatus(String idBusiness, String statusTF) {
        return (root, query, cb) -> {
            var u = root.join("username"); // EntityUser
            return cb.and(
                    cb.equal(cb.upper(root.get("idBusiness").get("idBusiness")), idBusiness.toUpperCase()),
                    cb.equal(u.get("status"), statusTF)
            );
        };
    }

    /** Atajo: activos en empresa. */
    public static Specification<EntityEmployee> activeInBusiness(String idBusiness) {
        return byBusinessAndStatus(idBusiness, "T");
    }

    /** Atajo: inactivos en empresa. */
    public static Specification<EntityEmployee> inactiveInBusiness(String idBusiness) {
        return byBusinessAndStatus(idBusiness, "F");
    }

    /** Empleados activos que SÍ pertenecen al comité (ambas FKs presentes). */
    public static Specification<EntityEmployee> inCommittee(String idBusiness) {
        return (root, query, cb) -> {
            var u = root.join("username"); // EntityUser

            Predicate byBusiness = cb.equal(
                    cb.upper(root.get("idBusiness").get("idBusiness")),
                    idBusiness.toUpperCase()
            );

            Predicate inCommittee = cb.and(
                    cb.isNotNull(root.get("idCommitteePosition")),
                    cb.isNotNull(root.get("idCommitteeRole"))
            );

            Predicate active = cb.equal(u.get("status"), "T");

            return cb.and(byBusiness, inCommittee, active);
        };
    }

    /** Empleados que NO pertenece a la capacitación indicada (mismo negocio). */
    public static Specification<EntityEmployee> notInTraining(String idTraining) {
        return (root, query, cb) -> {
            //subconsulta EXISTS sobre EntityTrainingEmployee
            Subquery<Integer> sq = query.subquery(Integer.class);
            var te = sq.from(EntityTrainingEmployee.class);

            sq.select(cb.literal(1));
            sq.where(
                    cb.equal(te.get("idEmployee").get("idEmployee"), root.get("idEmployee")),
                    cb.equal(te.get("idTraining").get("idTraining"), idTraining),
                    // aseguramos que sea la MISMA empresa
                    cb.equal(te.get("idBusiness").get("idBusiness"), root.get("idBusiness").get("idBusiness"))
            );

            // "NO pertenece" => NOT EXISTS(subconsulta)
            return cb.not(cb.exists(sq));
        };
    }

    /** Empleados que pertenecen a la capacitación indicada (mismo negocio). */
    public static Specification<EntityEmployee> inTraining(String idTraining) {
        return (root, query, cb) -> {
            Subquery<Integer> sq = query.subquery(Integer.class);
            var te = sq.from(EntityTrainingEmployee.class);
            sq.select(cb.literal(1));
            sq.where(
                    cb.equal(te.get("idEmployee").get("idEmployee"), root.get("idEmployee")),
                    cb.equal(te.get("idTraining").get("idTraining"), idTraining),
                    cb.equal(te.get("idBusiness").get("idBusiness"), root.get("idBusiness").get("idBusiness"))
            );
            return cb.exists(sq); // SÍ pertenece
        };
    }
}