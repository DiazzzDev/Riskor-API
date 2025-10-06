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

    //Activos e inactivos por negocio según estatus de los usuarios
    //Usado solamente en el repositorio para facilitar implementación y escalabilidad
    Page<EntityEmployee> findByIdBusiness_IdBusinessAndUsername_Status(String idBusiness, String status, Pageable pageable);

    boolean existsByUsername_UsernameAndIdBusiness_IdBusiness(String username, String idBusiness);

    //Búsqueda por DUI
    Optional<EntityEmployee> findByDuiAndIdBusiness_IdBusiness(String dui, String idBusiness);

    Page<EntityEmployee> findByIdBusiness_IdBusinessAndIdCommitteePositionIsNotNullAndIdCommitteeRoleIsNotNullAndUsername_Status(String idBusiness, String status, Pageable pageable);

    Page<EntityEmployee> findByIdBusiness_IdBusinessAndIdCommitteePositionIsNullAndIdCommitteeRoleIsNullAndUsername_Status(String idBusiness, String status,Pageable pageable);

    //Consulta JPQL (Java Persistence Query Language)
    //Para buscar a todos los empleados activos que no están dentro de una capacitación específica
    /**
     * Descripción de la consulta:
     * -Se seleccionarán todos los empleados que sean de una misma empresa y estén activos
     * -"AND NOT EXISTS (...)" Busca empleados que no cumplan la condición dentro de la sub-consulta
     * -"SELECT 1 FROM EntityTrainingEmployee" Busca un registro training-employee que cumpla con las condiciones de los parámetros
     * -Solo se selecciona 1 para saber que existe, no para obtener los datos del empleado que está dentro
     */
    @Query("""
            SELECT e FROM EntityEmployee e
            WHERE e.idBusiness.idBusiness = :idBusiness
            AND e.username.status = 'T'
            AND NOT EXISTS (
                SELECT 1
                FROM EntityTrainingEmployee te
                WHERE te.idEmployee.idEmployee = e.idEmployee
                    AND te.idTraining.idTraining = :idTraining
                    AND te.idBusiness.idBusiness = :idBusiness
            )
            """)
    Page<EntityEmployee> findActiveEmployeesNotInTraining(String idBusiness, String idTraining, Pageable pageable);

    /**
     * Descripción de la consulta:
     * - Se seleccionan todos los empleados de una misma empresa (=:idBusiness) que estén activos (status = 'T').
     * - "AND EXISTS (...)" asegura que el empleado tiene AL MENOS un registro en EntityTrainingEmployee
     *   que coincide con el id del entrenamiento (=:idTraining) y la misma empresa.
     * - "SELECT 1 ..." solo verifica la existencia; no trae datos adicionales del registro hijo.
     * - Al partir desde EntityEmployee y usar EXISTS, no se generan duplicados aunque el empleado tenga
     *   múltiples filas en EntityTrainingEmployee para ese entrenamiento.
     *
     * Recomendación de índice (tabla física de la relación):
     * - Índice compuesto sobre (IDBUSINESS, IDTRAINING, IDEMPLOYEE) para acelerar la subconsulta.
     *
     * @param idBusiness  Identificador de la empresa (scope)
     * @param idTraining  Identificador del entrenamiento a verificar
     * @param pageable    Paginación y orden
     * @return Página de empleados activos que sí están inscritos en el entrenamiento indicado
     */
    @Query("""
      SELECT e FROM EntityEmployee e
      WHERE e.idBusiness.idBusiness = :idBusiness
        AND e.username.status = 'T'
        AND EXISTS (
          SELECT 1
          FROM EntityTrainingEmployee te
          WHERE te.idEmployee.idEmployee = e.idEmployee
            AND te.idTraining.idTraining = :idTraining
            AND te.idBusiness.idBusiness = :idBusiness
        )
    """)
    Page<EntityEmployee> findActiveEmployeesInTraining(String idBusiness, String idTraining, Pageable pageable);

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

    /**
     * Métodos para realizar búsqueda
     */
}
