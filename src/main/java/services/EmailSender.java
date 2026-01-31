package Services;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import Entite.Reservation;
import Utilis.LogManager;

import java.util.Date;
import java.io.File;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com"; // Serveur SMTP
    private static final int SMTP_PORT = 587; // Port SMTP
    private static final String USERNAME = "azizbouslimi10@gmail.com"; // Adresse e-mail
    private static final String PASSWORD = "bhug vteq zorn qpji"; // Mot de passe d'application

    // Méthode générique pour envoyer un e-mail
    public static void sendEmail(String recipient, String subject, String body, String attachmentPath) {
        // Propriétés pour la connexion SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            // Création du message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);

            // Création du contenu du message
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body + "\n\nDate: " + new Date().toString()); // Ajout de la date

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(attachmentPath)); // Ajout de l'image en pièce jointe

            // Assemblage du message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            // Envoi de l'e-mail
            Transport.send(message);
            LogManager logger = LogManager.getInstance();
            logger.info("E-mail Send with success to : " + recipient);
            // System.out.println("E-mail envoyé avec succès à : " + recipient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode 1 : Envoi avec contenu x1x1x1x1x1x1 et une image
    public static void sendEmailWelcomeAddUser(String recipient, String passcode , String attachmentPath) {
        sendEmail(recipient, "You Are welcome in Mybooking application \n", "Dear client ,\n thanks to create our account ,\n your verification code is " + passcode, attachmentPath);
    }

    public static void sendReservationConfirmationEmail(String recipient, Reservation reservation, String attachmentPath) {
    String subject = "Confirmation de votre réservation";
    String body = "Bonjour " + reservation.getUserEmail() + ",\n\n" +
                  "Votre réservation a été confirmée avec succès.\n" +
                  "Voici les détails de votre réservation :\n" +
                  "- Maison ID : " + reservation.getId() + "\n" +
                  "- Date de début : " + reservation.getDateDebut() + "\n" +
                  "- Date de fin : " + reservation.getDateFin() + "\n" +
                  "- Prix total : " + String.format("%.2f €", reservation.getReservationPrice()) + "\n\n" +
                  "Veuillez trouver votre reçu en pièce jointe.\n\nMerci de nous faire confiance.";
    sendEmail(recipient, subject, body, attachmentPath);
}


}
