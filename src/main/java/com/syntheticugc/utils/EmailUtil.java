package com.syntheticugc.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // Get email configuration from environment variables with fallback values
    private static String getEmailFrom() {
        return System.getenv("EMAIL_FROM") != null ? 
               System.getenv("EMAIL_FROM") : "contact.syntheticugc@gmail.com";
    }
    
    private static String getEmailPassword() {
        return System.getenv("EMAIL_PASSWORD") != null ? 
               System.getenv("EMAIL_PASSWORD") : "Jf@5S83xRn#D";
    }
    
    private static String getEmailTo() {
        return System.getenv("EMAIL_TO") != null ? 
               System.getenv("EMAIL_TO") : "udaraudawatte@gmail.com";
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java EmailUtil <subject> <body> <attachment_path> [isHtml]");
            System.exit(1);
        }

        String subject = args[0];
        String body = args[1];
        String attachmentPath = args[2];
        boolean isHtml = args.length > 3 && Boolean.parseBoolean(args[3]);

        File attachment = new File(attachmentPath);
        if (!attachment.exists()) {
            System.err.println("Attachment file not found: " + attachmentPath);
            System.exit(1);
        }

        sendTestReport(subject, body, new File[]{attachment}, isHtml);
    }

    public static void sendTestReport(String subject, String body, File[] attachments, boolean isHtml) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getEmailFrom(), getEmailPassword());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(getEmailFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmailTo()));
            message.setSubject(subject);

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            if (isHtml) {
                messageBodyPart.setContent(body, "text/html; charset=utf-8");
            } else {
                messageBodyPart.setText(body);
            }

            // Create the multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachments
            if (attachments != null) {
                for (File file : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Test report email sent successfully!");

        } catch (Exception e) {
            System.err.println("Failed to send test report email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Overloaded method for backward compatibility
    public static void sendTestReport(String subject, String body, File[] attachments) {
        sendTestReport(subject, body, attachments, false);
    }
} 