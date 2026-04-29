package edu.lpnu.saas.security;

import edu.lpnu.saas.model.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("orgSecurity")
public class OrgSecurity {

    public boolean hasMinRole(Long organizationId, String minRoleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        Role minRole = Role.valueOf(minRoleName);
        String prefix = "ORG_" + organizationId + "_";

        return auth.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith(prefix))
                .findFirst()
                .map(a -> {
                    String userRoleStr = a.getAuthority().replace(prefix, "");
                    Role userRole = Role.valueOf(userRoleStr);

                    return userRole.getLevel() >= minRole.getLevel();
                })
                .orElse(false);
    }
}