package az.jrs.sweet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("snsibov23@gmail.com");
        javaMailSender.setPassword("urqg lgov pcux zdli");

        javaMailSender.getJavaMailProperties().put("mail.smtp.auth", "true");
        javaMailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
        javaMailSender.getJavaMailProperties().put("mail.debug", "true");

        return javaMailSender;
    }

}
