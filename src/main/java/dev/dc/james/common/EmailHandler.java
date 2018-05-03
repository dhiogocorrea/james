package dev.dc.james.common;

import dev.dc.james.config.EmailConfiguration;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHandler {

    EmailConfiguration emailConfiguration;
    Session session;

    static final int PORT = 587;

    public EmailHandler(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "*");

        session = Session.getDefaultInstance(props);
    }

    public void sendEmail(String to, String subject, String body)
            throws NoSuchProviderException, MessagingException, UnsupportedEncodingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(emailConfiguration.getSender(),
                emailConfiguration.getDisplayName()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setHeader("Content-Type", "text/plain; charset=ISO-8859-1");
        msg.setText(body, "ISO-8859-1");

        Transport transport = session.getTransport();

        transport.connect(emailConfiguration.getHost(), emailConfiguration.getSender(),
                emailConfiguration.getPassword());

        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }
}
