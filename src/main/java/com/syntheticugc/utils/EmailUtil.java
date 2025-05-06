package com.syntheticugc.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // Hardcoded email configuration
    private static String getEmailFrom() {
        return "contact.syntheticugc@gmail.com";
    }
    
    private static String getEmailPassword() {
        return "Jf@5S83xRn#D";
    }
    
    private static String getEmailTo() {
        return "udaraudawatte@gmail.com";
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

            // Check if we have any test reports
            File surefireDir = new File("target/surefire-reports");
            if (!surefireDir.exists() || surefireDir.listFiles() == null || surefireDir.listFiles().length == 0) {
                System.out.println("No test reports found in target/surefire-reports directory");
                body = "No test reports were generated. This could be because:\n" +
                      "1. Tests were not executed\n" +
                      "2. Tests failed before generating reports\n" +
                      "3. Test reports directory is empty\n\n" +
                      "Original message: " + body;
            }

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

            // Add test report files if they exist
            if (surefireDir.exists()) {
                File[] reportFiles = surefireDir.listFiles((dir, name) -> 
                    name.endsWith(".html") || name.endsWith(".xml") || name.endsWith(".txt"));
                
                if (reportFiles != null) {
                    for (File report : reportFiles) {
                        MimeBodyPart reportPart = new MimeBodyPart();
                        reportPart.attachFile(report);
                        multipart.addBodyPart(reportPart);
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