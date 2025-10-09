package RiskOrganizationPTC2025.RISKOR_DevTeam.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

//Agregamos compontent a esta clase si queremos que Spring gestione su ciclo de vida, Spring lo convierte en un @bean y lo gestiona por nosotros
@Component
public class UtilsJWT {
    //Asignamos un valor a las variables CLAVE para la creación de la cookie a traves del aplication propperties
    @Value("${security.jwt.secret}")
    private String secret; //Clave secreta para firmar el token con HS256
    @Value("${security.jwt.issuer}")
    private String issuer; //Indica el emisor del token
    @Value("${security.jwt.expiration}")
    private long expirationTime; //Asigna el tiempo de vida del token - 30min

    //Método para poder enviar el valor de el tiempo de expiración al controlador
    public long getExpiracionMs() { return expirationTime; }

    //LLama al logger para mostrar mensajes importantes en consola de servidor
    private final Logger log = LoggerFactory.getLogger(UtilsJWT.class);

    public String create(String id, String email, String role, String business){
        /*
         * Esta parte es CLAVE, Aquí construye una clave simétrica para firmar el JWT con HS256
         * El secreto viene en Base64 desde la variable "secret", luego se decodifica a bytes,
         * De esos bytes se crea una SecretKey/Clave secreta válida para HS256 (HMAC + SHA256)
         */
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        //Se crea instancia de la fecha actual
        Date now = new Date();

        //Define la fecha de expiración sumando el tiempo actual en milisegundos
        Date expiration = new Date(now.getTime() + expirationTime);

        //Crea el token y es personalizado
        return Jwts.builder()
                .setId(id)                                                  //ID del JWT (Es único)
                .setIssuedAt(now)                                           //Fecha que fue creado el token
                .setSubject(email)                                          //Sujeto (usuario)
                .claim("id", id)

                //Creamos 2 claims, el primero declara el rol que tendrá el empleado (Ej. Gerente)
                .claim("role", role)
                //En el segundo indica la empresa en la que el empleado tendrá acceso
                .claim("business", business)

                .setIssuer(issuer)                                          //Emisor del token - Este proyecto de spring
                .setExpiration(expirationTime >= 0 ? expiration : null)     //Expiración (si es >= 0)
                .signWith(signingKey, SignatureAlgorithm.HS256)             //Firma con algoritmo HS256
                .compact();                                                 //Convierte a String compacto
    }

    public String getId(String jwt){
        return parseClaims(jwt).getId();
    }

    //Método para obtener el rol del usuario desde su token
    public String getRole(String token){
        //Para obtener el rol mandamos a llamar parseToken (Creado más abajo) y agregamos get() para conseguir el claim que necesitemos
        //Dentro de ese get, agrega el nombre del claim y String.class para el formato que es necesario devolver
        return parseToken(token).get("role", String.class);
    }

    //Método para obtener la empresa del usuario desde el token
    public String getBusiness(String token){
        return parseToken(token).get("business", String.class);
    }


    //Método para obtener los valores del token que necesitemos en otros métodos como "extractBusiness()"
    public Claims parseToken(String jwt) throws ExpiredJwtException, MalformedJwtException {
        return parseClaims(jwt);
    }

    public boolean validate(String token){
        try{
            //Obtenemos el valor de los claims
            parseClaims(token);
            //Si todo sale bien devuelve true, la única manera que esto no sea así es si al mandar a llamar el método lanza una excepción
            return true;
        }catch (JwtException | IllegalArgumentException e){
            //Si se lanza una excepción se menciona que el token es inválido
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    //Método encargado de extraer un JWT - Verificando su autenticidad y que no ha sido modificado
    private Claims parseClaims(String jwt) {
        //Construye un analizador (parser) que funciona como una herramienta para abrir y leer el token
        return Jwts.parserBuilder()
                //Envía el secreto al parser para firmar el token para verificar la firma del token (En base64 para ser leída correctamente su valor en formato binario)
                //Si la firma no coincide el proceso falla
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                //Capa extra de seguridad donde se verifica que el issuer/emisor del token sea el mismo con el que fue creado
                .requireIssuer(issuer)
                //Finaliza la construcción del parser con las config definidas
                .build()
                //Analiza el token y aplica las medidas de seguridad que tomamos
                .parseClaimsJws(jwt)
                //Aquí obtenemos el contenido del JWT (Devuelve todos los claims del token)
                .getBody();
    }
}
