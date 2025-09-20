package RiskOrganizationPTC2025.RISKOR_DevTeam.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class UtilPasswordGenerator {
    /**
     * Esta clase tiene la función de crear una cadena de carácteres segura, utilizada
     * en la generación de contraseñas por defecto al crear un empleado que se le enviarán por correo.
     *                                                                                  -Díaz
     */

    //Cargamos una variable de entorno y con ella se le asigna valor a esta variable
    @Value("${SALT}")
    private String salt;

    //Método que crea un String Seguro de 12 carácteres a partir de mezclar 2 cadenas manualmente
    public String generateSecureRandomString() {
        //Verificamos que la sal haya sido cargada correctamente
        if (salt == null) {
            throw new IllegalStateException("SALT no definido");
        }

        //Generamos un UUID (Universally Unique Identifier) aleatorio sin guiones
        String uuid = UUID.randomUUID().toString().replace("-", ""); // Elimina los guiones para mayor simplicidad

        //Entrelaza el salt y el UUID de forma aleatoria
        String mixedString = mixStrings(salt, uuid);

        try{
            //Hashea el string creado con SHA-256 y se guarda en un arreglo de bytes
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(mixedString.getBytes()); //"HA12L30" -> "34T69%$&$%&/$%"

            //Muestra en base64 el string final y recorta el hash para devolverlo con 12 carácteres
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 12);
        }catch (NoSuchAlgorithmException e){
            //Hacemos manejo de excepciones porque "getInstance" lo requiere
            throw new IllegalStateException("No está disponible SHA-256 en esta JVM", e);
        }
    }

    /**
     * Entrelaza aleatoriamente los caracteres de dos cadenas.
     * Si las cadenas tienen longitudes diferentes, los caracteres restantes de la cadena más larga
     * se añaden al final.
     * Por ejemplo, "HOLA" y "123" podría resultar en "H1O2L3A".
     */
    private String mixStrings(String s1, String s2) {
        //Declaramos una lista donde se va a guardar los carácteres de ambas cadenas de forma desordenada
        List<Character> resultList = new ArrayList<>();

        //Variables de longitud de cada cadena y para conocer la longitud de la cadena más larga
        int len1 = s1.length();
        int len2 = s2.length();
        int maxLength = Math.max(len1, len2);

        //Se va a repetir por cada vez que "i" no supere la longitud MAX de la cadena más larga
        for (int i = 0; i < maxLength; i++) {
            //Si "i" es menor que la longitud de la primera cadena...
            if (i < len1) {
                //Primera cadena
                //Será agregado el carácter de posición "i" de la primera cadena en la lista
                resultList.add(s1.charAt(i));
            }
            //El proceso se repite con la segunda cadena
            if (i < len2) {
                resultList.add(s2.charAt(i));
            }
        }

        //Mezcla el contenido de la lista de manera aleatoria
        Collections.shuffle(resultList);

        //Uso de StringBuilder para pasar la lista a la cadena segura
        StringBuilder builder = new StringBuilder();

        //Por cada carácter en la lista mezclada será añadido a la cadena
        for (Character c : resultList) {
            builder.append(c);
        }

        //Devolvemos la cadena creada en formato correcto lista para pasar por proceso de hash
        return builder.toString();
    }
}