package RiskOrganizationPTC2025.RISKOR_DevTeam.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro se ejecuta una vez por cada solicitud HTTP
 * Componente gestionado por Spring
 */
@Component //Agregamos component a esta clase si queremos que Spring gestione su ciclo de vida, Spring lo convierte en un @bean y lo gestiona por nosotros
public class UtilJWTCookieAuthFilter extends OncePerRequestFilter {
    @Autowired
    private UtilsJWT jwtUtils;

    //Declara variable para identificar la cookie con la que vamos a trabajar
    //Se declara así y no en el lugar donde lo usamos porque facilita mantenimiento, no debe cambiarse en ejecución (final/constante)
    private static final String AUTH_COOKIE_NAME = "authToken";

    @Override //Este método es protegido porque así es en la clase OncePerRequestFilter, para no exponer el ciclo del filtro públicamente
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Extraer token de la cookie
        String token = extractTokenFromCookie(request);

        if (token == null || token.isBlank()) {
            SecurityContextHolder.clearContext();
            sendError(response, "Token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try{
            //Obtenemos los claims mandando a llamar el método parsetoken() del JWTUtils
            Claims claims = jwtUtils.parseToken(token);

            //Guardamos los datos en la request, estos son obtenidos del token, útiles para personalizar el acceso a nuestros endpoints (Empresa/Rol)
            request.setAttribute("auth.role", jwtUtils.extractRole(token));
            request.setAttribute("auth.business", jwtUtils.extractBusiness(token));
            request.setAttribute("auth.email", claims.getSubject());

            //Authorities y autenticación
            //Convierte a string auth.role y lo guarda en una variable
            String role = (String) request.getAttribute("auth.role");

            //Construye Authentication con el rol del token, se agrega el prefijo "ROLE_" porque spring así lo espera
            var authority = new SimpleGrantedAuthority("ROLE_" + role);

            //Construimos el token de autenticación de Spring Security.
            //Usamos el "subject" del token (email del empleado)
            //Credentials: null porque ya validamos el JWT
            //Colocamos al último argumento como lista porque UsernamePasswordAuthenticationToken espera un Colletion...
            var authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, List.of(authority));

            //A partir de aquí @PreAuthorize(...) podrá evaluar roles que hemos asignado en UsernamePasswordAuthenticationToken
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Se continua con el resto de filtros por cada solicitud HTTP
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            SecurityContextHolder.clearContext();
            sendError(response, "Token inválido", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        if (response.isCommitted()) return;
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\":\"%s\",\"status\":%d}", message, status));
    }

    //Busca la cookie "authToken" y extrae su JWT o devuelve null si no fue encontrado (Ej. No hubo login, cookie expirada, etc)
    private String extractTokenFromCookie(HttpServletRequest request) {
        //Pide al navegador recibir todas las cookies que envió, guardándolas en un arreglo
        Cookie[] cookies = request.getCookies();

        //Sin cookies se retorna null, no hay cookies, no hay token
        if (cookies == null) return null;

        //Retornamos un arreglo de las cookies con stream para manejarlas de manera más eficiente
        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName())) //Pasamos un filtro donde nos quedaremos con una cookie específica, y la buscamos por AUTH_COOKIE_NAME
                .findFirst() //Tomará la primera cookie que coincida
                .map(Cookie::getValue) //Con map se extrae solo su valor, en este caso el valor del token
                .orElse(null); //Si no se encontró una cookie con el nombre esperado retorna null
    }

    //Método para centralizar las rutas que no requieren autenticación, así que vamos a sobreescribir un método de Once Per Request Filter
    //Ya no usamos isPublicEndpoint() porque
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) return true;
        if ("/api/auth/login".equals(p) && "POST".equalsIgnoreCase(method)) return true;
        if ("/api/businessInfo/postBusinessInfo".equals(p) && "POST".equalsIgnoreCase(method)) return true;
        return false;
    }
}