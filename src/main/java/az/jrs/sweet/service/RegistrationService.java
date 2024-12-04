package az.jrs.sweet.service;

import az.jrs.sweet.config.SecurityConfig;
import az.jrs.sweet.dto.request.MailRequest;
import az.jrs.sweet.dto.request.SignUpRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.dto.response.SignUpResponse;
import az.jrs.sweet.exception.UserOperationException;
import az.jrs.sweet.mapstruck.UserMapper;
import az.jrs.sweet.model.entity.User;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.repository.UserRepository;
import az.jrs.sweet.util.CacheUtil;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

import static az.jrs.sweet.constant.ExceptionConstants.*;
import static az.jrs.sweet.model.enums.Exception.*;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheUtil cacheUtil;
    private final JwtService jwtService;
    private final SecurityConfig securityConfig;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest, Language language) {
        Optional<User> existingUser = userRepository.findByEmail(signUpRequest.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (Boolean.TRUE.equals(user.getIsRegistered())) {
                throw new UserOperationException(
                        getTranslationByLanguage(EMAIL_ALREADY_EXISTS_CODE, language),
                        EMAIL_ALREADY_EXISTS);
            } else {
                throw new UserOperationException(
                        getTranslationByLanguage(EMAIL_NOT_VERIFIED_CODE, language),
                        EMAIL_NOT_VERIFIED);
            }
        }

        User user = userMapper.signUpRequestToUser(signUpRequest);
        String encodedPassword = securityConfig.passwordEncoder().encode(signUpRequest.getPassword());
        user.setPassword(encodedPassword);
        user.setIsRegistered(false);
        userRepository.save(user);
        processOTPAndSendEmail(signUpRequest, language);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new SignUpResponse(accessToken, refreshToken);
    }


    private void processOTPAndSendEmail(SignUpRequest signUpRequest, Language language) {
        String otp = generateOTP();
        String message = String.format("OTP: %s", otp);
        MailRequest mailDto = userMapper.signUpRequestToMailRequest(signUpRequest, message);
        emailService.sendEmail(mailDto, language);
        cacheUtil.writeToCache(signUpRequest.getEmail() + ":" + otp, "NEW_REGISTRATION", 2L);
    }

    public void verifyOtp(VerifyOtpRequest verifyOtpRequest,
                          Language language) {
        String email = verifyOtpRequest.getEmail();
        String otp = verifyOtpRequest.getOtp();


        String attemptsKey = "otp_attempts:" + email;

        Object attemptsObject = cacheUtil.onlyReadWithKeyFromCache(attemptsKey);
        String attemptsStr = (attemptsObject instanceof String) ? (String) attemptsObject : null;

        int attempts = (attemptsStr != null) ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= 3) {
            throw new UserOperationException(
                    getTranslationByLanguage(MAX_ATTEMPTS_EXCEEDED_CODE, language),
                    MAX_ATTEMPTS_EXCEEDED);
        }

        boolean isCorrectOtp = validateOtp(email, otp);
        if (!isCorrectOtp) {
            attempts++;
            cacheUtil.writeToCache(attemptsKey, String.valueOf(attempts), 1L);

            throw new UserOperationException(
                    getTranslationByLanguage(OTP_INCORRECT_CODE, language),
                    OTP_INCORRECT);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserOperationException(
                        getTranslationByLanguage(USER_NOT_FOUND_CODE, language),
                        USER_NOT_FOUND));

        user.setIsRegistered(true);
        userRepository.save(user);
        cacheUtil.deleteFromCache(attemptsKey);

    }

    private boolean validateOtp(String email, String otp) {
        String otpKey = email + ":" + otp;
        return cacheUtil.onlyReadWithKeyFromCache(otpKey) != null;
    }

    private String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }

    public void retryOtp(String email,Language language) {
        String otp = generateOTP();
        String message = String.format("OTP: %s", otp);
        MailRequest mailDto = userMapper.emailToMailRequest(email, message);
        emailService.sendEmail(mailDto, language);
        cacheUtil.writeToCache(email + ":" + otp, "RETRY_OTP", 2L);
    }
}
//  if (jwtService.isTokenExpired(accessToken)) {
//        throw new UserOperationException(
//        getTranslationByLanguage(TOKEN_EXPIRED_CODE, language),
//TOKEN_EXPIRED);
//        }
//
//Claims claims = jwtService.parseToken(accessToken);
//String tokenEmail = claims.getSubject();
//
//        if (!tokenEmail.equals(email)) {
//        throw new UserOperationException(
//        getTranslationByLanguage(TOKEN_EMAIL_MISMATCH_CODE, language),
//TOKEN_EMAIL_MISMATCH);
//        }
