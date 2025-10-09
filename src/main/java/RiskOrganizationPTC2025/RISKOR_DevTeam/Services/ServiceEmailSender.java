package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ServiceEmailSender {
    private final JavaMailSender mailSender; //Interfaz que ofrece Spring que facilita envío de correos

    public ServiceEmailSender(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String sender;

    public void sendWelcomeTemplate(String toEmail, String subject, String appName, String name, String username, String temporaryPassword, String businessName, String createdAt){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            String html = loadTemplate("/templates/user.html");

            Map<String, String> vars = new HashMap<>();
            vars.put("appName", safe(appName));
            vars.put("name", safe(name));
            vars.put("username", safe(username));
            vars.put("temporaryPassword", safe(temporaryPassword));
            vars.put("businessName", safe(businessName));
            vars.put("createdAt", safe(createdAt));
            vars.put("year", String.valueOf(java.time.Year.now().getValue()));

            html = applyVars(html, vars);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new IllegalStateException("Error enviando correo: " + e.getMessage(), e);
        }
    }

    public void sendNewTrainingTemplate(String toEmail, String subject, String appName, String employeeName, String trainingTitle, String trainingDescription, String startAt, String location, String modality){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setSubject(subject != null ? subject : "Se te ha unido a la capacitación");

            String html = loadTemplate("/templates/newtraining.html");

            Map<String, String> vars = new HashMap<>();
            vars.put("appName", safe(appName));
            vars.put("employeeName", safe(employeeName));
            vars.put("trainingTitle", safe(trainingTitle));
            vars.put("trainingDescription", safe(trainingDescription));
            vars.put("startAt", safe(startAt));
            vars.put("location", safe(location));
            vars.put("modality", safe(modality));
            vars.put("year", String.valueOf(java.time.Year.now().getValue()));

            html = applyVars(html, vars);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new IllegalStateException("Error enviando correo de capacitación: " + e.getMessage(), e);
        }
    }

    private String loadTemplate(String path) throws Exception {
        try (var in = Objects.requireNonNull(
                ServiceEmailSender.class.getResourceAsStream(path),
                "No se encontró la plantilla: " + path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String applyVars(String html, Map<String, String> vars) {
        String result = html;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            String key = e.getKey();
            String val = java.util.regex.Matcher.quoteReplacement(e.getValue());

            //Patrón [[${key}]]
            String thymeleafLike = "\\[\\[\\$\\{" + java.util.regex.Pattern.quote(key) + "}]]";
            result = result.replaceAll(thymeleafLike, val);

            //Patrón moustache simple: {{key}}
            String moustache = "\\{\\{" + java.util.regex.Pattern.quote(key) + "}}";
            result = result.replaceAll(moustache, val);
        }
        return result;
    }
}
