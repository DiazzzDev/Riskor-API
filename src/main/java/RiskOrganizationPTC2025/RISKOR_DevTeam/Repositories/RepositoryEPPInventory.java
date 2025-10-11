package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEPPInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryEPPInventory extends JpaRepository<EntityEPPInventory, String>,
                                                JpaSpecificationExecutor<EntityEPPInventory> {
    List<EntityEPPInventory> findByIdBusiness_IdBusiness(String idBusiness);
    Page<EntityEPPInventory> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);
    Optional<EntityEPPInventory> findByIdEPPInventoryAndIdBusiness_IdBusiness(String idEPPInventory, String idBusiness);

    boolean existsByIdEPPInventoryAndIdBusiness_IdBusiness(String idEPPInventory, String idBusiness);

    void deleteByIdEPPInventoryAndIdBusiness_IdBusiness(String idEPPInventory, String upperCase);

    /**
     * Esencial para asegurar que las siguientes consultas en la misma transacción vean los datos actualizados inmediatamente.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
       UPDATE EntityEPPInventory e
          SET e.availableQuantity = e.availableQuantity - :qty
        WHERE e.idEPPInventory = :id
          AND e.idBusiness.idBusiness = :idBusiness
          AND e.availableQuantity >= :qty
    """)
    int decrementAvailable(@Param("idBusiness") String idBusiness,
                           @Param("id") String idEPPInventory,
                           @Param("qty") int qty);

    @Modifying(flushAutomatically = true)
    @Query("""
        UPDATE EntityEPPInventory e
            SET e.availableQuantity = e.availableQuantity + :qty
        WHERE e.idEPPInventory = :id
            AND e.idBusiness.idBusiness = :idBusiness
            AND (e.availableQuantity + :qty) <= e.totalQuantity
        """)
    int incrementAvailable(@Param("idBusiness") String idBusiness,
                           @Param("id") String idEPPInventory,
                           @Param("qty") int qty);

}
