package edu.lpnu.saas.service;

import edu.lpnu.saas.exception.types.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String baseUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = baseUrl + "/auth/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("resetLink", resetLink);

        String htmlBody = templateEngine.process("reset-password", context);
        String subject = "Відновлення пароля - Sentio";

        sendEmail(toEmail, subject, htmlBody);
    }

    public void sendInvitationWithCredentialsEmail(String toEmail, String orgName, String password) {
        Context context = new Context();
        context.setVariable("orgName", orgName);
        context.setVariable("email", toEmail);
        context.setVariable("password", password);
        context.setVariable("loginLink", baseUrl + "/auth/login");

        String htmlBody = templateEngine.process("invite-new-user", context);
        sendEmail(toEmail, "Запрошення в " + orgName + " та дані для входу", htmlBody);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Помилка відправки email на {}", toEmail, e);
            throw new EmailSendException("Критична помилка при відправці пошти");
        }
    }
}