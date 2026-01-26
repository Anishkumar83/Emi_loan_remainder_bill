package org.emiloanwithbill.util;

import org.emiloanwithbill.config.EmailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SendEmail {
    public void sendEmailWithAttachment(String to,
                                        String subject,
                                        String pdfPath,
                                        String htmlContent) throws Exception{
        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.get("mail.smtp.host"));
        props.put("mail.smtp.port", EmailConfig.get("mail.smtp.port"));
        props.put("mail.smtp.auth", EmailConfig.get("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", EmailConfig.get("mail.smtp.starttls.enable"));

        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");


        String username = EmailConfig.get("mail.username");
        String password = EmailConfig.get("mail.password");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message  message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(htmlContent, "text/html; charset=UTF-8");

        MimeBodyPart attachment = new MimeBodyPart();
        attachment.attachFile(new File(pdfPath));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachment);

        message.setContent(multipart);
        Transport.send(message);

    }
    public String loadHtml(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}
