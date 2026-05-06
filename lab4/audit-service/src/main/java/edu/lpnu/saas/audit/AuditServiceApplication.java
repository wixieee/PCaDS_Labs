package edu.lpnu.saas.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {
        "edu.lpnu.saas.audit",
        "edu.lpnu.saas.common"
}, exclude = {UserDetailsServiceAutoConfiguration.class})
public class AuditServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}
