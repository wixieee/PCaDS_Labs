package edu.lpnu.saas.common.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtPrincipal {
    private Long id;
    private String email;

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtPrincipal principal) {
            return principal.getId();
        }
        throw new IllegalStateException("Спроба отримати ID користувача без активної авторизації");
    }
}