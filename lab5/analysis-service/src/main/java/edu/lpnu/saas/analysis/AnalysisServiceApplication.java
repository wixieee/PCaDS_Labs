package edu.lpnu.saas.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {
        "edu.lpnu.saas.analysis",
        "edu.lpnu.saas.common"
}, exclude = {UserDetailsServiceAutoConfiguration.class})
public class AnalysisServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalysisServiceApplication.class, args);
    }
}