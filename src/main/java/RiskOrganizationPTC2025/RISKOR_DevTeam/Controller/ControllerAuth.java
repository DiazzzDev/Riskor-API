package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOLogin;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAuth;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilsJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    //Endpoint ME, encargado de realizar GET a variables de entorno de usuario en principio de Login
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "No autenticado"
                        ));
            }

            // Manejar diferentes tipos de Principal
            String username;
            Collection<? extends GrantedAuthority> authorities;

            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
                authorities = userDetails.getAuthorities();
            } else {
                username = authentication.getName();
                authorities = authentication.getAuthorities();
            }

            Optional<EntityEmployee> userOpt = objServiceA.getEmployeeByMail(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Usuario no encontrado"
                        ));
            }

            EntityEmployee user = userOpt.get();

            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user", Map.of(
                            "id", user.getIdEmployee(),
                            "username", user.getUsername(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "employeeMail", user.getEmployeeEmail(),
                            "DUI", user.getDui(),
                            "photoEmployee", user.getPhoto(),
                            "employeePosition", user.getIdEmployeePosition().getEmployeePosition(),
                            "comitteeRole", user.getIdCommitteeRole().getCommitteRoleName(),
                            "authorities", authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                    )
            ));

        } catch (Exception e) {
            //log.error("Error en /me endpoint: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "authenticated", false,
                            "message", "Error obteniendo datos de usuario"
                    ));
        }
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

            //Usamos long porque eso devuelve el método, luego será convertido a int
            //Porque lo necesitamos entero para la creación de la cookie
            long maxAgeSeconds = objUtilJWT.getExpiracionMs() / 1000; //Dividimos entre 1000 para convertir de milisegundos a segundos

            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);                //Aquí se cambiará a TRUE hasta que se haga consumo de la api en HTTPS (Producción) no HTTP (desarrollo)
            cookie.setPath("/");                    //Se aplica para toda la api esta cookie creada
            cookie.setMaxAge((int) maxAgeSeconds);  //Convertimos de long a entero para colocar el tiempo de vida del token en segundos

            //Agregamos la cookie a la respuesta
            response.addCookie(cookie);
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