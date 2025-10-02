package RiskOrganizationPTC2025.RISKOR_DevTeam.Controller;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOLogin;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Services.ServiceAuth;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilsJWT;
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

    @PostMapping("/logout") //Indicamos que en la respuesta no debemos devolver nada con Void
    private ResponseEntity<Void> logout(HttpServletResponse response){
        //Manda a llamar al método que eliminará la cookie
        clearCookie(response);
        //Devolvemos un 204, de que el cierre de sesión fue realizado correctamente
        return ResponseEntity.noContent().build();
    }

    /**
     *  IMPORTANTE:
     *  La razón por la que hay métodos que no son endpoints dentro del controller y no el service es por el parámetro HttpServletResponse
     *  ya que pertenecen al ciclo de vida del request/response (se menciona por el método de abajo) y si se coloca en el service se pierde
     *  el principio de separación de capas
     */
    private void addTokenCookie(HttpServletResponse response, String email) {
        objServiceA.getEmployeeByCredentials(email).ifPresent(employee -> {
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

            //Crear la cookie como un String formateado
            String cookieValue = String.format(
                    "authToken=%s; " +
                            "Path=/; " + //Se aplica para toda la API esta cookie creada
                            "HttpOnly; " + //Protege la cookie de accesos JavaScript
                            "Secure; " + //Asegura la cookie cuando el API está en HTTPS (desarrollo debe ser TRUE solo en producción)
                            "SameSite=None; " + //Desde otro dominio (Ejemplo vercel)
                            "Max-Age=%d; " + //Duración de la cookie en segundos
                            "Domain=riskor-370e22badbf5.herokuapp.com", // Asegura que la cookie sea válida solo para el dominio
                    token, maxAgeSeconds
            );

            //Agregar la cookie a la respuesta de la cabecera HTTP
            response.addHeader("Set-Cookie", cookieValue);
            response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
        });
    }

    /**
     *  IMPORTANTE:
     *  La razón por la que hay métodos que no son endpoints dentro del controller y no el service es por el parámetro HttpServletResponse
     *  ya que pertenecen al ciclo de vida del request/response (se menciona por el método de abajo) y si se coloca en el service se pierde
     *  el principio de separación de capas
     */
    private void clearCookie(HttpServletResponse response) {
        //Creamos la cookie con el mismo nombre y dominio que la cookie original
        String cookieValue = String.format(
                "authToken=; " +             //Valor vacío para eliminar
                        "Path=/; " +
                        "HttpOnly; " +
                        "Secure; " +
                        "SameSite=None; " +
                        "Max-Age=0; " +              //Expiración inmediata
                        "Domain=riskor-370e22badbf5.herokuapp.com" // Mismo dominio que la cookie original
        );

        //Agregamos la cabecera para eliminar la cookie
        response.addHeader("Set-Cookie", cookieValue);
        response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
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

            if (authentication.getPrincipal() instanceof UserDetails ud) {
                username = ud.getUsername();
                authorities = ud.getAuthorities();
            } else {
                username = authentication.getName();
                authorities = authentication.getAuthorities();
            }

            Optional<EntityEmployee> userOpt = objServiceA.getEmployeeByCredentials(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Usuario no encontrado"
                        ));
            }

            EntityEmployee user = userOpt.get();
            String committeeRole = (user.getIdCommitteeRole() != null) ? user.getIdCommitteeRole().getCommitteRoleName() : "No posee un rol en el CSSO";

            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user", Map.of(
                            "id", user.getIdEmployee(),
                            "username", user.getUsername().getUsername(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "employeeMail", user.getEmployeeEmail(),
                            "DUI", user.getDui(),
                            "photoEmployee", user.getPhoto(),
                            "employeePosition", user.getIdEmployeePosition().getEmployeePosition(),
                            "committeeRole", committeeRole,
                            "authorities", authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList()))
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
}