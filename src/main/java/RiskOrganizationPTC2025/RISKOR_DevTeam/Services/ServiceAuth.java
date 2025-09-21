package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    public boolean Login(String email, String password){
        //Se busca al empleado que le pertenece el correo recibido como argumento
        Optional<EntityEmployee> employeeFounded = repoE.findActiveByEmployeeMail(email);

        //Si no fue encontrado directamente se deniega el acceso
        if (employeeFounded.isEmpty()) return false;

        //Ahora que se encontró el empleado, vamos a obtener la contraseña de la entidad encontrada
        //get() sirve para obtener el valor real dentro del Optional (Este caso la entidad del empleado)
        String hash = employeeFounded.get().getUsername().getPassword();

        //Finalmente vamos a verificar si la contraseña ingresada es la misma que tiene el usuario
        return passwordEncoder.matches(password, hash); //Si todo sale bien dará true otorgando acceso, caso contrario se negará el login
    }

    public Optional<EntityEmployee> getEmployeeByMail(String mail) {
        return repoE.findActiveByEmployeeMail(mail);
    }
}
