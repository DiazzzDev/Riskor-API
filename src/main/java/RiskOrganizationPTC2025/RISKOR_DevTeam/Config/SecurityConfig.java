package RiskOrganizationPTC2025.RISKOR_DevTeam.Config;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilJWTCookieAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Con esto habilitamos el uso de PreAuthorize para cada método (Permite agregarla sin esta anotación pero no se aplica)
public class SecurityConfig {
    @Autowired
    private UtilJWTCookieAuthFilter jwtCookieAuthFilter;

    //Método que se centra en la cadena de filtros de seguridad y reglas de autorización
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //Aplica la configuración de CORS que realizamos dentro de nuestra aplicación, esto porque al conectarlo al frontend va a dar problema de CORS si no se agrega
                .cors(withDefaults())

                //Desactiva Cross Site Request Forgery
                .csrf(csrf -> csrf.disable())

                //Ahora, estas son las reglas de autorización por las rutas HTTP
                .authorizeHttpRequests(auth -> auth

                        //Endpoints que no van a requerir el JWT
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()

                        //Endpoints que si requieren estar autenticado con el JWT
                        .requestMatchers("/api/auth/me").authenticated()

                        //Todos los demás endpoints deben requerir JWT, sino 401
                        .anyRequest().authenticated()
                )

                //Sin estado de sesión: cada request debe traer su JWT (no se guarda nada en servidor) Esto porque usa autenticación por cookies
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Será agregado primero el filtro de cookies de la clase UtilJWTCookieAuthFilter antes que el filtro por formulario por defecto
                //Esto como otra capa de seguridad
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //Usamos el AuthenticationManager como bean, esto si se decide aplicar autenticación de manera manual (No es obligatorio)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}