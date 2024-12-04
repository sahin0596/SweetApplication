package az.jrs.sweet.service;

import az.jrs.sweet.dto.request.MailRequest;
import az.jrs.sweet.exception.UserOperationException;
import az.jrs.sweet.model.enums.Language;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static az.jrs.sweet.constant.ExceptionConstants.EMAIL_NOT_ACTIVE;
import static az.jrs.sweet.model.enums.Exception.EMAIL_NOT_ACTIVE_CODE;
import static az.jrs.sweet.model.enums.Exception.getTranslationByLanguage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @SneakyThrows
    public void sendEmail(MailRequest mail,Language language) {
       try {
           MimeMessage msg = javaMailSender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(msg, true,"UTF-8");
           mapDetails(mail, helper,language);
           javaMailSender.send(msg);
       }
       catch (Exception e) {
           throw new UserOperationException(
                   getTranslationByLanguage(EMAIL_NOT_ACTIVE_CODE, language),
                   EMAIL_NOT_ACTIVE
           );
    }
    }

    @SneakyThrows
    private void mapDetails(MailRequest mail, MimeMessageHelper helper, Language language) {
        try {
            helper.setFrom(new InternetAddress("no-reply@accounts.google.com"));
            helper.setTo(new String[]{mail.getTo()});
            helper.setText(mail.getMessage());

            if (mail.getCc() != null && !mail.getCc().isEmpty()) {
                helper.setCc(mail.getCc().toArray(new String[0]));
            }
            helper.setSubject(mail.getSubject());

        }
        catch (Exception e) {
            throw new UserOperationException(
                    getTranslationByLanguage(EMAIL_NOT_ACTIVE_CODE, language),
                    EMAIL_NOT_ACTIVE
            );
        }}
}
