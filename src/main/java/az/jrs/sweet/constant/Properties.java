package az.jrs.sweet.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@Getter
@FieldDefaults(level =PRIVATE)

public class Properties {
    @Value("${mail.host}")
     String host;

    @Value("${mail.port}")
    Integer port;

    @Value("${mail.username}")
    String username;

    @Value("${mail.password}")
    String password;


}