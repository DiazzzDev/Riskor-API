package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.spec;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPInventory;
import org.springframework.data.jpa.domain.Specification;

public final class EPPInventorySpecs {
    private EPPInventorySpecs() {}

    public static Specification<EntityEPPInventory> inBusiness(String idBusiness) {
        return (root, query, cb) -> {
            if (idBusiness == null || idBusiness.isBlank()) return cb.conjunction();
            return cb.equal(root.get("idBusiness").get("idBusiness"), idBusiness.toUpperCase());
        };
    }

    public static Specification<EntityEPPInventory> nameContains(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("nameEPP")), like);
        };
    }
}
