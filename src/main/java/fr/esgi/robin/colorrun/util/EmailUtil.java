package fr.esgi.robin.colorrun.util;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.*;
import com.sendgrid.helpers.mail.objects.*;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {

    private static final String SENDGRID_API_KEY;
    private static final String EMAIL_FROM;
    private static final String FROM_NAME;

    static {
        Properties props = new Properties();
        try {
            props.load(EmailUtil.class.getClassLoader().getResourceAsStream("mail.properties"));
            SENDGRID_API_KEY = props.getProperty("sendgrid.api-key");
            EMAIL_FROM = "colorrun@outlook.fr";
            FROM_NAME = "ColorRun";
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mail properties", e);
        }
    }
    String recoverysendgrid = "XSU6GH29U6ZDGAHUJE7QTTLH";

    /**
     * Sends a password reset email to the user
     */
    public static boolean sendPasswordResetEmail(String email, String resetUrl, String prenom) {
        try {
            Email from = new Email(EMAIL_FROM, FROM_NAME);
            Email to = new Email(email);
            String subject = "Réinitialisation de votre mot de passe ColorRun";
            String emailContent = createPasswordResetEmailTemplate(prenom, resetUrl);
            Content content = new Content("text/html", emailContent);

            Mail mail = new Mail(from, subject, to, content);

            return sendWithSendGrid(mail);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String createPasswordResetEmailTemplate(String prenom, String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; }" +
                ".content { background: #f8f9fa; padding: 30px; }" +
                ".button { display: inline-block; background: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                ".footer { text-align: center; color: #6c757d; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<h1>🏃‍♀️ ColorRun</h1>" +
                "<p>Réinitialisation de mot de passe</p>" +
                "</div>" +
                "<div class=\"content\">" +
                "<h2>Bonjour " + prenom + ",</h2>" +
                "<p>Vous avez demandé la réinitialisation de votre mot de passe ColorRun.</p>" +
                "<p>Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>" +
                "<a href=\"" + resetUrl + "\" class=\"button\">Réinitialiser mon mot de passe</a>" +
                "<p><strong>⏰ Ce lien expire dans 1 heure.</strong></p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>L'équipe ColorRun</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Simule l'envoi d'un email de vérification de compte
     */
    public static boolean sendVerificationEmail(String email, String verificationUrl, String prenom) {
        try {
            // Simulation de l'envoi d'email
            System.out.println("📧 SIMULATION EMAIL VERIFICATION - Envoi à: " + email);
            System.out.println("👤 Destinataire: " + prenom);
            System.out.println("🔗 Lien de vérification: " + verificationUrl);
            System.out.println("📝 Contenu de l'email:");
            System.out.println("---");
            System.out.println("Bonjour " + prenom + ",");
            System.out.println("");
            System.out.println("Bienvenue sur ColorRun ! Pour activer votre compte, cliquez sur le lien ci-dessous :");
            System.out.println("");
            System.out.println(verificationUrl);
            System.out.println("");
            System.out.println("Ce lien expire dans 24 heures.");
            System.out.println("");
            System.out.println("L'équipe ColorRun");
            System.out.println("---");
            System.out.println("✅ Email de vérification envoyé avec succès (SIMULATION)");

            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email de vérification: " + e.getMessage());
            return false;
        }
    }

    private static boolean sendWithSendGrid(Mail mail) throws IOException {
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        int statusCode = response.getStatusCode();

        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("✅ Email envoyé avec succès à: " +
                    mail.getPersonalization().get(0).getTos().get(0).getEmail());
            return true;
        } else {
            System.err.println("❌ Échec de l'envoi de l'email. Code: " + statusCode);
            System.err.println("Response Body: " + response.getBody());
            return false;
        }
    }

    public static boolean sendContactResponseEmail(String email, String prenom, String reponse) {
        try {
            Email from = new Email(EMAIL_FROM, FROM_NAME);
            Email to = new Email(email);
            String subject = "Réponse à votre demande de contact ColorRun";
            String contentHtml = "<p>Bonjour " + prenom + ",</p>"
                + "<p>Votre demande a été traitée. Voici la réponse de l'équipe ColorRun :</p>"
                + "<blockquote style='background:#f8f9fa;padding:10px;border-left:4px solid #007bff;'>" + reponse + "</blockquote>"
                + "<p>Sportivement,<br>L'équipe ColorRun</p>";

            Content content = new Content("text/html", contentHtml);
            Mail mail = new Mail(from, subject, to, content);

            return sendWithSendGrid(mail);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email de réponse contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendContactConfirmationEmail(String email, String prenom) {
        try {
            Email from = new Email(EMAIL_FROM, FROM_NAME);
            Email to = new Email(email);
            String subject = "Votre demande de contact ColorRun a été prise en compte";
            String contentHtml = "<p>Bonjour " + prenom + ",</p>"
                + "<p>Votre demande a bien été prise en compte par l'équipe ColorRun. Nous vous répondrons dans les plus brefs délais.</p>"
                + "<p>Sportivement,<br>L'équipe ColorRun</p>";

            Content content = new Content("text/html", contentHtml);
            Mail mail = new Mail(from, subject, to, content);

            return sendWithSendGrid(mail);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email de confirmation contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 