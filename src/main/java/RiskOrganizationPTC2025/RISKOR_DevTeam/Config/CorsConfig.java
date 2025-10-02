package RiskOrganizationPTC2025.RISKOR_DevTeam.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        //Uso de cookies
        config.setAllowCredentials(true);

        //Orígenes desde donde se pueden hacer solicitudes
        config.addAllowedOrigin("http://127.0.0.1:5501");
        config.addAllowedOrigin("http://127.0.0.1:5502");
        config.addAllowedOrigin("https://sistemaweb-beta.vercel.app/"); //Dominio web definido por vercel
        config.addAllowedOrigin("https://riskor.app"); //Dominio web
        config.addAllowedOrigin("https://localhost");
        config.addAllowedOrigin("https://riskor-370e22badbf5.herokuapp.com");

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

        //Cabeceras
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Access-Control-Request-Method");
        config.addAllowedHeader("Access-Control-Request-Headers");
        config.addAllowedHeader("Cookie");
        config.addAllowedHeader("Set-Cookie");

        config.setExposedHeaders(Arrays.asList("Set-Cookie", "Cookie", "Authorization", "Content-Disposition"));

        //Tiempo de cache para preflight requests
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // También crea el CorsConfigurationSource para SecurityConfig
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //Uso de cookies
        configuration.setAllowCredentials(true);

        //Orígenes desde donde se pueden hacer solicitudes
        configuration.addAllowedOrigin("http://127.0.0.1:5501");
        configuration.addAllowedOrigin("http://127.0.0.1:5502");
        configuration.addAllowedOrigin("https://riskor-370e22badbf5.herokuapp.com");
        configuration.addAllowedOrigin("https://sistemaweb-beta.vercel.app/");
        configuration.addAllowedOrigin("https://riskor.app");
        configuration.addAllowedOrigin("https://localhost");

        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type","Authorization","Accept","Origin","X-Requested-With"));

        configuration.addExposedHeader("Set-Cookie");
        configuration.addExposedHeader("Cookie");
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}