package az.jrs.sweet.service;

import az.jrs.sweet.constant.ExceptionConstants;
import az.jrs.sweet.dto.request.MailRequest;
import az.jrs.sweet.dto.request.SignUpRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.exception.UserOperationException;
import az.jrs.sweet.mapstruck.UserMapper;
import az.jrs.sweet.model.entity.OTP;
import az.jrs.sweet.model.entity.User;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.repository.OTPRepository;
import az.jrs.sweet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static az.jrs.sweet.model.enums.Exception.*;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final EmailService emailService;
    private final OTPService otpService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OTPRepository otpRepository;

    public void signUp(SignUpRequest signUpRequest, Language language) {
        Long emailId = userRepository.findIdByEmail(signUpRequest.getEmail());

        if (Objects.isNull(emailId)) {
            User user = userMapper.signUpRequestToUser(signUpRequest);
            userRepository.save(user);
            processOTPAndSendEmail(signUpRequest);
        } else if (otpRepository.existsByUserIdAndIsRegisteredTrue(emailId)) {
            throw new UserOperationException(
                    getTranslationByLanguage(EMAIL_ALREADY_EXISTS, language),
                    ExceptionConstants.EMAIL_ALREADY_EXISTS);
        } else {
            processOTPAndSendEmail(signUpRequest);
        }
    }

    private void processOTPAndSendEmail(SignUpRequest signUpRequest) {
        String otp = otpService.generateOTP();

        String message = String.format("OTP: %s", otp);
        MailRequest mailDto = userMapper.signUpRequestToMailRequest(signUpRequest, message);

        emailService.sendEmail(mailDto);

        otpService.saveOTP(signUpRequest.getEmail(), otp);
    }

    public void verifyOtp(VerifyOtpRequest verifyOtpRequest,
                          Language language) {
        Long userId = userRepository.findIdByEmail(verifyOtpRequest.getEmail());

        if (otpRepository.existsByUserIdAndIsRegisteredTrue(userId)) {
            throw new UserOperationException(
                    getTranslationByLanguage(EMAIL_ALREADY_EXISTS, language),
                    ExceptionConstants.EMAIL_ALREADY_EXISTS);        }

        Optional<OTP> latestOtp = otpRepository.findLatestOtpByUserIdAndConditions(userId);

        if (latestOtp.isPresent()) {
            OTP otp = latestOtp.get();

            if (otp.getOtp().equals(verifyOtpRequest.getOtp())) {
                otp.setRegistered(true);
                otpRepository.save(otp);
            } else {
                otp.setRetryCount(otp.getRetryCount() + 1);
                otpRepository.save(otp);
                throw new UserOperationException(
                        getTranslationByLanguage(OTP_INCORRECT, language),
                        ExceptionConstants.OTP_INCORRECT);            }
        } else {
            throw new UserOperationException(
                    getTranslationByLanguage(OTP_NOT_FOUND, language),
                    ExceptionConstants.OTP_NOT_FOUND);
        }
    }
}
