package edu.lpnu.saas.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "edu.lpnu.saas.auth",
        "edu.lpnu.saas.common"
}, exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableScheduling
public class IamServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }
}