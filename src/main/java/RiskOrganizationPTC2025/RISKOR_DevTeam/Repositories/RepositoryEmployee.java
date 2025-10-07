package RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryEmployee extends JpaRepository<EntityEmployee, String>,
                                            JpaSpecificationExecutor<EntityEmployee> {
    //Buscar por ID filtrado con empresa
    //1er IdBusiness: Nombre del atributo en EntityEmployee (ManyToOne), 2do: Campo PK en EntityBusinessInfo
    Optional<EntityEmployee> findByIdEmployeeAndIdBusiness_IdBusiness(String idEmployee, String idBusiness); //Este método también se usa para dashboard

    //Todos los empleados por cada empresa - GET
    Page<EntityEmployee> findByIdBusiness_IdBusiness(String idBusiness, Pageable pageable);

    EntityEmployee findByIdBusiness_IdBusiness(String idBusiness);

    boolean existsByUsername_UsernameAndIdBusiness_IdBusiness(String username, String idBusiness);

    //Búsqueda por DUI
    Optional<EntityEmployee> findByDuiAndIdBusiness_IdBusiness(String dui, String idBusiness);

    //Consulta JPQL (Java Persistence Query Language)
    //Query para hacer Login a traves del correo de los empleados, verificando si el usuario está activo y obtenemos
    @Query("""
        SELECT e FROM EntityEmployee e
            JOIN FETCH e.username u
            LEFT JOIN FETCH e.idRole r
            JOIN FETCH e.idBusiness b
        WHERE (UPPER(e.employeeEmail) = UPPER(:login)
            OR UPPER(u.username)   = UPPER(:login))
        AND u.status = 'T'
    """)
    Optional<EntityEmployee> findActiveByLogin(String login);

    //Método para mandar a llamar para el AUTH ME (Por las cargas perezosas - LAZY FETCH)
    @Query("""
        SELECT e FROM EntityEmployee e
            JOIN FETCH e.idEmployeePosition p
            LEFT JOIN FETCH e.idCommitteeRole  c
            LEFT JOIN FETCH e.idCommitteePosition cp
            JOIN FETCH e.username u
        WHERE (LOWER(e.employeeEmail) = LOWER(:login)
            OR LOWER(u.username)      = LOWER(:login))
        AND u.status = 'T'
    """)
    Optional<EntityEmployee> findMyInfo(@Param("login") String login);
}
