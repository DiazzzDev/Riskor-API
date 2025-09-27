package RiskOrganizationPTC2025.RISKOR_DevTeam.Config;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Utils.UtilJWTCookieAuthFilter;
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
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Con esto habilitamos el uso de PreAuthorize para cada método (Permite agregarla sin esta anotación pero no se aplica)
public class SecurityConfig {
    private final UtilJWTCookieAuthFilter jwtCookieAuthFilter; //Variable statica
    private final CorsConfigurationSource corsConfigurationSource; //Inyecta CorsConfigurationSource

    // Complemento de las variables de arriba
    public SecurityConfig(UtilJWTCookieAuthFilter jwtCookieAuthFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }
    //Método que se centra en la cadena de filtros de seguridad y reglas de autorización
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))    //Con esto se esta configurando los CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()             //Permite los preflight requests
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()     //Esto es para que a este endpoint acceda cualquiera
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/me").authenticated()   // Este endpoint es para ver la info del usuario logeado
                        .anyRequest().authenticated()
                )

                //Sin estado de sesión: cada request debe traer su JWT porque usa autenticación por cookies
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Será agregado primero el filtro de cookies de la clase UtilJWTCookieAuthFilter antes que el filtro por formulario por defecto
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //Usamos el AuthenticationManager como bean, esto si se decide aplicar autenticación de manera manual (No es obligatorio)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}