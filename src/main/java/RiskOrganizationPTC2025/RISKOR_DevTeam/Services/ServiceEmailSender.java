package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class ServiceEmailSender {
    private final JavaMailSender mailSender; //Interfaz que ofrece Spring que facilita envío de correos

    public ServiceEmailSender(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String sender;

    public String sendEmail(String toEmail, String subject, String body){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            //SimpleMailMessage messageOld = new SimpleMailMessage();

            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            try (var inputStream = Objects.requireNonNull(ServiceEmailSender.class.getResourceAsStream("/templates/user.html"))) {
                helper.setText(
                        new String(inputStream.readAllBytes(), StandardCharsets.UTF_8),
                        true
                );
            }catch (Exception e) {
                //Manejar error de lectura de plantilla
                return "Error al leer la plantilla HTML: " + e.getMessage();
            }

            mailSender.send(message);
            return "success!";
        } catch (MailException e) {
            //Manejar excepciones específicas de Spring Mail
            return "Error de envío de correo: " + e.getMessage();
        } catch (Exception e) {
            //Manejar otras excepciones (ej. MessagingException)
            return "Error general al configurar el mensaje: " + e.getMessage();
        }
    }
}
