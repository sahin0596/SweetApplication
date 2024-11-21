package az.jrs.sweet.service;

import az.jrs.sweet.dto.request.MailRequest;
import az.jrs.sweet.dto.request.SignUpRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.exception.UserOperationException;
import az.jrs.sweet.mapstruck.UserMapper;
import az.jrs.sweet.model.entity.User;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.repository.UserRepository;
import az.jrs.sweet.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static az.jrs.sweet.constant.ExceptionConstants.*;
import static az.jrs.sweet.model.enums.Exception.*;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final EmailService emailService;
    private final OTPService otpService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheUtil cacheUtil;

    public String signUp(SignUpRequest signUpRequest, Language language) {
        Long emailId = userRepository.findIdByEmail(signUpRequest.getEmail());

        if (Objects.isNull(emailId)) {
            User user = userMapper.signUpRequestToUser(signUpRequest);
            userRepository.save(user);
            processOTPAndSendEmail(signUpRequest);
        } else {
            throw new UserOperationException(
                    getTranslationByLanguage(EMAIL_ALREADY_EXISTS_CODE, language),
                    EMAIL_ALREADY_EXISTS);
        }
        return signUpRequest.getEmail();

    }

    private void processOTPAndSendEmail(SignUpRequest signUpRequest) {
        String otp = otpService.generateOTP();

        String message = String.format("OTP: %s", otp);
        MailRequest mailDto = userMapper.signUpRequestToMailRequest(signUpRequest, message);

        emailService.sendEmail(mailDto);

        cacheUtil.writeToCache(signUpRequest.getEmail() + ":"+ otp,"NEW_REGISTRATION", 2L);

        otpService.saveOTP(signUpRequest.getEmail(), otp);
    }

    public void verifyOtp(VerifyOtpRequest verifyOtpRequest, Language language) {
        String email = verifyOtpRequest.getEmail();
        String otp = verifyOtpRequest.getOtp();

        String attemptsKey = "otp_attempts:" + email;
        String attemptsStr = String.valueOf(cacheUtil.getAttemptsFromCache(attemptsKey));
        int attempts = (attemptsStr != null) ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= 3) {
            throw new UserOperationException(
                    getTranslationByLanguage(MAX_ATTEMPTS_EXCEEDED_CODE, language),
                    MAX_ATTEMPTS_EXCEEDED);
        }

        String otpKey = email + ":" + otp;

        // Validate OTP
        boolean isCorrectOtp = cacheUtil.onlyReadWithKeyFromCache(otpKey) != null;
        if (!isCorrectOtp) {
            cacheUtil.incrementCacheValue(attemptsKey);
            throw new UserOperationException(
                    getTranslationByLanguage(OTP_INCORRECT_CODE, language),
                    OTP_INCORRECT);
        }

        cacheUtil.deleteFromCache(attemptsKey);
        cacheUtil.deleteFromCache(otpKey);
    }
}