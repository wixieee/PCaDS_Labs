package edu.lpnu.saas.aop;

import edu.lpnu.saas.security.JwtPrincipal;
import edu.lpnu.saas.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final ActivityLogService activityLogService;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @AfterReturning("@annotation(auditAction)")
    public void logActivity(JoinPoint joinPoint, AuditAction auditAction) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (auth != null && auth.getPrincipal() instanceof JwtPrincipal principal) {
                userId = principal.getId();
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

            activityLogService.logAction(userId, organizationId, auditAction.action(), ipAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}