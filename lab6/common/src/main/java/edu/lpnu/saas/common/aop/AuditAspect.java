package edu.lpnu.saas.common.aop;

import edu.lpnu.saas.common.dto.AuditMessage;
import edu.lpnu.saas.common.security.JwtPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final RabbitTemplate rabbitTemplate;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @AfterReturning("@annotation(auditAction)")
    public void logActivity(JoinPoint joinPoint, AuditAction auditAction) {
        try {
            Long userId = null;
            try {
                userId = JwtPrincipal.getCurrentUserId();
            } catch (Exception ignored) {
            }

            String ipAddress = "unknown";
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty()) {
                    ipAddress = request.getRemoteAddr();
                }
            }

            Long organizationId = null;
            if (!auditAction.orgId().isEmpty()) {
                StandardEvaluationContext context = new StandardEvaluationContext();
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String[] paramNames = discoverer.getParameterNames(signature.getMethod());
                Object[] args = joinPoint.getArgs();

                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }

                organizationId = parser.parseExpression(auditAction.orgId()).getValue(context, Long.class);
            }

            AuditMessage message = AuditMessage.builder()
                    .userId(userId)
                    .organizationId(organizationId)
                    .action(auditAction.action())
                    .ipAddress(ipAddress)
                    .build();

            String routingKey = "audit.action." + auditAction.action().toLowerCase();

            rabbitTemplate.convertAndSend("audit.exchange", routingKey, message);
            log.debug("Відправлено подію аудиту в RabbitMQ: {}", routingKey);

        } catch (Exception e) {
            log.error("Помилка генерації події аудиту: ", e);
        }
    }
}