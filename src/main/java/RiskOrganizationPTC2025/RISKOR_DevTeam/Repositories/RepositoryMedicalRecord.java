package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityMedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryMedicalRecord extends JpaRepository<EntityMedicalRecord, String> {
    //Listado general paginado POR EMPRESA
    Page<EntityMedicalRecord> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    //Lista paginada de expedientes por empleado dentro de una empresa
    Page<EntityMedicalRecord> findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(
            String idBusiness, String idEmployee, Pageable pageable);

    //Para garantizar 1 expediente por empleado
    boolean existsByIdEmployee_IdEmployee(String idEmployee);
}
