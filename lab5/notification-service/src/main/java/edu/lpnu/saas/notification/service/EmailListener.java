package edu.lpnu.saas.notification.service;

import edu.lpnu.saas.common.dto.PasswordResetEvent;
import edu.lpnu.saas.common.dto.UserInvitedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "notification.email.queue")
public class EmailListener {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String baseUrl;

    @RabbitHandler
    public void handlePasswordReset(PasswordResetEvent event) {
        log.info("Отримано запит на скидання пароля для: {}", event.getEmail());

        String resetLink = baseUrl + "/auth/reset-password?token=" + event.getToken();

        Context context = new Context();
        context.setVariable("resetLink", resetLink);

        String htmlBody = templateEngine.process("reset-password", context);
        sendEmail(event.getEmail(), "Відновлення пароля - Sentio", htmlBody);
    }

    @RabbitHandler
    public void handleUserInvited(UserInvitedEvent event) {
        log.info("Отримано запит на відправку запрошення для: {}", event.getEmail());

        Context context = new Context();
        context.setVariable("orgName", event.getOrgName());
        context.setVariable("email", event.getEmail());
        context.setVariable("password", event.getPassword());
        context.setVariable("loginLink", baseUrl + "/auth/login");

        String htmlBody = templateEngine.process("invite-new-user", context);
        sendEmail(event.getEmail(), "Запрошення в " + event.getOrgName() + " та дані для входу", htmlBody);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.debug("Email успішно відправлено на {}", toEmail);
        } catch (MessagingException e) {
            log.error("Помилка відправки email на {}. Повідомлення буде повернуто в чергу.", toEmail, e);
            throw new RuntimeException("Помилка при відправці пошти", e);
        }
    }
}