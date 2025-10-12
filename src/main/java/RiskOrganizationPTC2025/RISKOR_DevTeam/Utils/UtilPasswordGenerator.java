package RiskOrganizationPTC2025.RISKOR_DevTeam.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

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

    //Estas variables se encargaran del ENVÍO DE PIN, al igual que la implementación de la correcta seguridad
    //Estas anotaciones PARTICULARES, se mantienen así dentro de la clase, aunque perfectamente podrían estar en el application.properties
    @Value("${pin.length:6}")
    private int pinLength;

    @Value("${pin.ttl.minutes:10}")
    private int defaultTtlMinutes;

    @Value("${pin.max.attempts:5}")
    private int maxAttempts;

    @Value("${pin.resend.cooldown.seconds:60}")
    private int resendCooldownSeconds;

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

    /* --------- MÉTODOS PARA EL ENVÍO DE PIN, ACTUALMENTE DEFENDIBLES --------- */
    //Clases de SEGURIDAD, envío de PIN
    private static final SecureRandom secureRandom = new SecureRandom();
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);

    //HashMAP, servirá para guardar el PIN en la API
    private final ConcurrentHashMap<String, PinEntry> pinMap = new ConcurrentHashMap<>();

    //Sirve para limpiar lo que reste de la creación del PIN por memoria y rendimiendo, clases de seguridad
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    //Mensaje a utilizar, PROPOSITO del envío
    private static final String PURPOSE = "PASSWORD_RESET";

    public UtilPasswordGenerator() {
        //Limpia expirados cada 60s, partiendo del TIEMPO en correo
        cleaner.scheduleAtFixedRate(this::cleanupExpiredEntries, 60, 60, TimeUnit.SECONDS);
    }

    //MÉTODO PARA LA CREACIÓN DEL PIN, a prueba de BALAS
    public String createPin(String emailRaw, Integer minutesOptional) {
        String email = normalizeEmail(emailRaw);
        int minutes = (minutesOptional != null && minutesOptional > 0) ? minutesOptional : defaultTtlMinutes;
        String key = keyFor(email, PURPOSE);
        Instant now = Instant.now();

        PinEntry existing = pinMap.get(key);
        if (existing != null) {
            long secondsSince = Duration.between(existing.lastSentAt, now).getSeconds();
            if (secondsSince < resendCooldownSeconds) {
                throw new IllegalStateException("COOLDOWN_ACTIVE:" + (resendCooldownSeconds - secondsSince));
            }
            if (existing.resendCount >= 3) { // límite simple diario (puedes ajustar)
                throw new IllegalStateException("MAX_RESENDS_REACHED");
            }
        }

        //Genera PIN y lo hashea
        String pin = generateNumericPin(pinLength);
        String hash = bcrypt.encode(pin);
        Instant expiresAt = now.plus(Duration.ofMinutes(minutes));

        pinMap.compute(key, (k, old) -> {
            if (old == null) {
                PinEntry p = new PinEntry(hash, now, expiresAt, maxAttempts, now, PURPOSE);
                p.resendCount = 1;
                return p;
            } else {
                old.hash = hash;
                old.createdAt = now;
                old.expiresAt = expiresAt;
                old.lastSentAt = now;
                old.attempts = 0;
                old.resendCount = old.resendCount + 1;
                old.used = false;
                return old;
            }
        });

        //Retornamos el PIN (solo para que el controller lo incluya en el correo)
        return pin;
    }

    //MÉTODO DE VERIFICACIÓN DEL PIN DE SEGURIDAD ENVIADO (similar a un GET)
    public VerifyResult verifyPin(String emailRaw, String pin) {
        String email = normalizeEmail(emailRaw);
        String key = keyFor(email, PURPOSE);

        PinEntry entry = pinMap.get(key);
        if (entry == null) return VerifyResult.fail("Código inválido o expirado", 0);

        Instant now = Instant.now();
        if (entry.expiresAt.isBefore(now)) {
            pinMap.remove(key);
            return VerifyResult.fail("Código expirado", 0);
        }

        boolean ok = bcrypt.matches(pin, entry.hash);
        if (ok) {
            pinMap.remove(key);
            return VerifyResult.ok("Código verificado");
        } else {
            PinEntry updated = pinMap.computeIfPresent(key, (k, old) -> {
                old.attempts = old.attempts + 1;
                if (old.attempts >= old.maxAttempts) old.used = true;
                return old;
            });

            if (updated == null) return VerifyResult.fail("Código inválido o expirado", 0);

            int left = Math.max(0, updated.maxAttempts - updated.attempts);
            if (updated.used) {
                pinMap.remove(key);
                return VerifyResult.blocked("Bloqueado por demasiados intentos");
            } else {
                return VerifyResult.fail("Código inválido", left);
            }
        }
    }

    /* --------- UTILS NECESARIO DENTRO DE ESTA CLASE, ayudará de MEJOR MANERA el manejo del HASHMAP, guardando el PIN --------- */
    //Verifica la entrada del PIN
    private static class PinEntry {
        String hash;
        Instant createdAt;
        Instant expiresAt;
        int attempts;
        final int maxAttempts;
        Instant lastSentAt;
        int resendCount;
        boolean used;
        String purpose;

        PinEntry(String hash, Instant createdAt, Instant expiresAt, int maxAttempts, Instant lastSentAt, String purpose) {
            this.hash = hash;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.attempts = 0;
            this.maxAttempts = maxAttempts;
            this.lastSentAt = lastSentAt;
            this.resendCount = 0;
            this.used = false;
            this.purpose = purpose;
        }
    }

    //Verifica la SALIDA del PIN
    public static class VerifyResult {
        public final boolean ok;
        public final boolean blocked;
        public final String message;
        public final int attemptsLeft;
        private VerifyResult(boolean ok, boolean blocked, String message, int attemptsLeft) {
            this.ok = ok; this.blocked = blocked; this.message = message; this.attemptsLeft = attemptsLeft;
        }
        public static VerifyResult ok(String m) { return new VerifyResult(true, false, m, 0); }
        public static VerifyResult fail(String m, int left) { return new VerifyResult(false, false, m, left); }
        public static VerifyResult blocked(String m) { return new VerifyResult(false, true, m, 0); }
    }
    //NORMALIZAR EL CORREO, al igual que el PROPOSITO, solamente se maneja dentro de la API, NO se envía tal cual
    private String normalizeEmail(String e) {
        return e == null ? null : e.trim().toLowerCase(Locale.ROOT);
    }
    private String keyFor(String email, String purpose) {
        return normalizeEmail(email) + "|" + purpose;
    }

    //Genera un PIN sencillo, que luego se HASHEA
    public String generateNumericPin(int length) {
        if (length < 4 || length > 8) throw new IllegalArgumentException("length must be 4..8");
        int max = (int) Math.pow(10, length);
        int v = secureRandom.nextInt(max);
        return String.format("%0" + length + "d", v);
    }

    //Para limpiar LO QUE RESTE del PIN de seguridad creado
    private void cleanupExpiredEntries() {
        Instant now = Instant.now();
        for (Iterator<Map.Entry<String, PinEntry>> it = pinMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, PinEntry> e = it.next();
            PinEntry p = e.getValue();
            if (p.used || p.expiresAt.isBefore(now)) it.remove();
        }
    }
}