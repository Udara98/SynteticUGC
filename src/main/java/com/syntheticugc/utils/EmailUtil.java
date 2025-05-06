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
        String email = System.getenv("EMAIL_FROM");
        if (email == null || email.trim().isEmpty()) {
            email = "contact.syntheticugc@gmail.com";
        }
        return email.trim();
    }
    
    private static String getEmailPassword() {
        String password = System.getenv("EMAIL_PASSWORD");
        if (password == null || password.trim().isEmpty()) {
            password = "Jf@5S83xRn#D";
        }
        return password.trim();
    }
    
    private static String getEmailTo() {
        String email = System.getenv("EMAIL_TO");
        if (email == null || email.trim().isEmpty()) {
            email = "udaraudawatte@gmail.com";
        }
        return email.trim();
    }

    private static void validateEmailAddress(String email) throws AddressException {
        if (email == null || email.trim().isEmpty()) {
            throw new AddressException("Email address cannot be empty");
        }
        new InternetAddress(email).validate();
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

        try {
            // Validate email addresses before proceeding
            String fromEmail = getEmailFrom();
            String toEmail = getEmailTo();
            validateEmailAddress(fromEmail);
            validateEmailAddress(toEmail);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, getEmailPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
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
                    if (file.exists()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        attachmentPart.attachFile(file);
                        multipart.addBodyPart(attachmentPart);
                    } else {
                        System.err.println("Warning: Attachment file not found: " + file.getAbsolutePath());
                    }
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Test report email sent successfully!");

        } catch (AddressException e) {
            System.err.println("Invalid email address: " + e.getMessage());
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("Failed to send test report email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while sending test report email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Overloaded method for backward compatibility
    public static void sendTestReport(String subject, String body, File[] attachments) {
        sendTestReport(subject, body, attachments, false);
    }
} 