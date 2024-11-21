package az.jrs.sweet.config;

import az.jrs.sweet.constant.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailSenderConfig {

    private final Properties properties;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(properties.getHost());
        javaMailSender.setPort(properties.getPort());
        javaMailSender.setUsername(properties.getUsername());
        javaMailSender.setPassword(properties.getPassword());

        javaMailSender.getJavaMailProperties().put("mail.smtp.auth", "true");
        javaMailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");

        return javaMailSender;
    }

}
