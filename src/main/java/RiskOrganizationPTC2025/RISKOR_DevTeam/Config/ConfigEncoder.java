package RiskOrganizationPTC2025.RISKOR_DevTeam.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //Para el uso de @Beans
public class ConfigEncoder {
    //Variables de config de argon2
    private final int SaltSize = 16;                    //Especificamos la cantidad de BITS para la clave SAL
    private final int HashSize = 32;                    //Especificamos la cantidad de BITS de la clave HASH
    private final int DegreeThreadsParallelism = 8;     //Número de HILOS a utilizar
    private final int Iterations = 10;                  //Especificamos el número de iteraciones (cuantas veces se repetirá
    private final int MemorySize = 65536;               //Especificamos el espacio de memoria que utilizará (RAM)

    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
            SaltSize, HashSize, DegreeThreadsParallelism, MemorySize, Iterations
    );

    @Bean
    public PasswordEncoder HashPassword(){ // Debido a eso no colocamos una configuración verbosa definiendo la versión de argon que queremos utilizar
        return new Argon2PasswordEncoder(SaltSize, HashSize, DegreeThreadsParallelism, MemorySize, Iterations);
    }

    //Método que nos va a permitir verificar si la contraseña que está recibiendo es la misma que la que existe en la DB
    public boolean verifyPassword(String hashDB, String password) {
        return encoder.matches(hashDB, password); //Se usa matches debido a las diferentes dependencias, en este proyecto se usa bouncy castle
    }
}
