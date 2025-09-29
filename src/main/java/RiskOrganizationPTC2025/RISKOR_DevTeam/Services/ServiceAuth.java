package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Clase encargada de la autenticación de usuarios a traves del empleado dueño de la cuenta.
 * Su función principal es verificar si un empleado puede iniciar sesión con su correo y contraseña.
 */

@Service
public class ServiceAuth {
    //Inyectamos las clases
    @Autowired
    private RepositoryEmployee repoE;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean Login(String credentials, String password){
        //Se busca al empleado que le pertenece el correo recibido como argumento
        var employee = repoE.findActiveByLogin(credentials); //Se usa var por legibilidad pero es un Optional<EntityEmployee>

        //Si no fue encontrado directamente se deniega el acceso
        if (employee.isEmpty()) return false;

        //Ahora que se encontró el empleado, vamos a obtener la contraseña de la entidad encontrada
        //get() sirve para obtener el valor real dentro del Optional (Este caso la entidad del empleado)
        String hash = employee.get().getUsername().getPassword();

        //Finalmente vamos a verificar si la contraseña ingresada es la misma que tiene el usuario
        return passwordEncoder.matches(password, hash); //Si todo sale bien dará true otorgando acceso, caso contrario se negará el login
    }

    public Optional<EntityEmployee> getEmployeeByCredentials(String credentials) {
        return repoE.findActiveByLogin(credentials);
    }

    //Indicamos que solamente se hará lectura en este método, garantizando que no habrá modificaciones a la DB
    @Transactional(readOnly = true) //También funciona para optimizar lectura de datos
    public Optional<EntityEmployee> getEmployeeForMe(String login) {
        //Mandamos a llamar al método que va a obtener toda la info del empleado
        return repoE.findActiveByLoginWithJoins(login);
    }
}
