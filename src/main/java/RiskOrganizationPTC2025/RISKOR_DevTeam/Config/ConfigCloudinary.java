package RiskOrganizationPTC2025.RISKOR_DevTeam.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloudinary {
    @Bean //Bean indica que este método se manda a llamar solo y se usa con @Configuration
    public Cloudinary cloudinary(){

        //Crea un MAP para guardar la clave: valor de la config que requiere Cloudinary
        Map<String, String> config = new HashMap<>();

        //Cargamos los valores del ENV y los asignamos al mapa
        config.put("cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"));
        config.put("api_key", System.getenv("CLOUDINARY_API_KEY"));
        config.put("api_secret", System.getenv("CLOUDINARY_API_SECRET"));

        return new Cloudinary(config); //Retornamos la configuración ya hecha
    }
}