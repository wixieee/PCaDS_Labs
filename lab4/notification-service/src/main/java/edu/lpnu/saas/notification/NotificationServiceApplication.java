package edu.lpnu.saas.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {
        "edu.lpnu.saas.notification",
        "edu.lpnu.saas.common.config"
}, exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
