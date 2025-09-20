package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityMedicalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryMedicalHistory extends JpaRepository<EntityMedicalHistory, String> {
    //Listado paginado POR EMPRESA
    Page<EntityMedicalHistory> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    // Listado paginado por expediente, dentro de la empresa
    Page<EntityMedicalHistory> findByIdBusiness_IdBusinessAndIdMedicalRecord_IdMedicalRecord(String idBusiness, String idMedicalRecord, Pageable pageable);

    // Búsqueda puntual validando empresa
    Optional<EntityMedicalHistory> findByIdMedicalHistoryAndIdBusiness_IdBusiness(String idMedicalHistory, String idBusiness);

    // Borrado seguro por empresa
    long deleteByIdMedicalHistoryAndIdBusiness_IdBusiness(String idMedicalHistory, String idBusiness);
}
