package az.jrs.sweet.service;

import az.jrs.sweet.dto.request.MailRequest;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @SneakyThrows
    public void sendEmail(MailRequest mail) {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true,"UTF-8");
        mapDetails(mail, helper);
        javaMailSender.send(msg);
    }

    @SneakyThrows
    private void mapDetails(MailRequest mail, MimeMessageHelper helper) {
        helper.setFrom(new InternetAddress("no-reply@accounts.google.com"));
        helper.setTo(new String[]{mail.getTo()});
        helper.setText(mail.getMessage());

        if (mail.getCc() != null && !mail.getCc().isEmpty()) {
            helper.setCc(mail.getCc().toArray(new String[0]));
        }
        helper.setSubject(mail.getSubject());

    }
}