package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOLogin;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAuth;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilsJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ControllerAuth {
    //Iniciamos inyectando el service
    @Autowired
    private ServiceAuth objServiceA;
    //Inyectamos el JWT
    @Autowired
    private UtilsJWT objUtilJWT;

    //Método POST que crea la sesión al usuario, otorgandole acceso a los recursos
    @PostMapping("/login")
    private ResponseEntity<String> login(@Valid @RequestBody DTOLogin dtoLogin, HttpServletResponse response){
        //Validamos con el método creado en el service que las credenciales ingresadas pertenezcan a un usuario válido para dar acceso
        if(!objServiceA.Login(dtoLogin.getCredentials(), dtoLogin.getPassword())){
            //Si no se encuentra un usuario con las credenciales ingresadas manda un 401 (Unauthorized)
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
        //Si las credenciales fueron válidas crea una cookie y la agregamos a la respuesta
        //Manda a llamar al método para crear la cookie y manda como argumentos la respuesta para poder crear la cookie
        //El email del empleado que ingresó y el valor booleano de este si desea mantener su sesión con un recuérdame
        addTokenCookie(response, dtoLogin.getCredentials());
        return ResponseEntity.ok("Inicio de sesión exitoso");
    }

    @PostMapping("/logout") //Indicamos que en la respuesta no debemos devolver nada con Void
    private ResponseEntity<Void> login(HttpServletResponse response){
        //Manda a llamar al método que eliminará la cookie
        clearCookie(response);
        //Devolvemos un 204, de que el cierre de sesión fue realizado correctamente
        return ResponseEntity.noContent().build();
    }

    private void addTokenCookie(HttpServletResponse response, String email) {
        objServiceA.getEmployeeByMail(email).ifPresent(employee -> {
            //Del empleado obtiene el nombre de su ROL dentro de la aplicación para manejar su acceso y guardarlo en la cookie
            //Obtiene el ID business para evitar que el usuario realice acciones en una empresa que no corresponde

            String token = objUtilJWT.create(
                    employee.getIdEmployee(),
                    employee.getEmployeeEmail(),
                    employee.getIdRole().getRoleName(),
                    employee.getIdBusiness().getIdBusiness()
            );

            String cookieValue = String.format(
                    "authToken=%s; " +
                            "Path=/; " + //Se aplica para toda la api esta cookie creada
                            "HttpOnly; " +
                            "Secure; " + //Aquí se cambiará a TRUE hasta que se haga consumo de la api en HTTPS (Producción) no HTTP (desarrollo)
                            "SameSite=None; " +
                            "MaxAge=86400; " + //Cookie para 1 día
                            "Domain=riskor-370e22badbf5.herokuapp.com",
                    token
            );

            response.addHeader("Set-Cookie", cookieValue);
            response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
        });
    }

    private void clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);                //Aquí se cambiará a TRUE hasta que se haga consumo de la api en HTTPS (Producción) no HTTP (desarrollo)
        cookie.setPath("/");                    //Se aplica para toda la api esta cookie creada
        cookie.setMaxAge(0);  //Convertimos de long a entero para colocar el tiempo de vida del token en segundos

        //Agregamos la cookie a la respuesta
        response.addCookie(cookie);
    }
}
