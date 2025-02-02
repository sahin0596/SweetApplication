package az.jrs.sweet.service;

import az.jrs.sweet.config.SecurityConfig;
import az.jrs.sweet.dto.request.LoginRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public SignUpResponse signUp(SignUpRequest signUpRequest, String idempotencyKey, Language language) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new UserOperationException(
                    getTranslationByLanguage(MISSING_IDEMPOTENCY_KEY_CODE, language),
                    MISSING_IDEMPOTENCY_KEY);
        }
        String cacheKey = "idempotency:" + idempotencyKey;

        if (cacheUtil.onlyReadWithKeyFromCache(cacheKey) != null) {
            throw new UserOperationException(
                    getTranslationByLanguage(DUPLICATE_REQUEST_CODE, language),
                    DUPLICATE_REQUEST);
        }
        cacheUtil.writeToCache(cacheKey, "IN_PROGRESS", 5L);

        try {
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

            cacheUtil.writeToCache(cacheKey, "COMPLETED", 60L);

            return new SignUpResponse(accessToken, refreshToken);
        } catch (Exception e) {
            cacheUtil.deleteFromCache(cacheKey);
            throw e;
        }
    }


    private void processOTPAndSendEmail(SignUpRequest signUpRequest, Language language) {
        String otp = generateOTP();
        String message = String.format("OTP: %s", otp);
        MailRequest mailDto = userMapper.signUpRequestToMailRequest(signUpRequest, message);
        emailService.sendEmail(mailDto, language);
        cacheUtil.writeToCache(signUpRequest.getEmail() + ":" + otp, "NEW_REGISTRATION", 2L);
    }

    public void verifyOtp(String otp,
                          String accessToken,
                          Language language) {
        String email = extractEmailFromToken(accessToken,language);

        if (email == null) {
            throw new UserOperationException(
                    getTranslationByLanguage(ACCESS_TOKEN_INVALID_CODE, language),
                    ACCESS_TOKEN_INVALID);
        }

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
            cacheUtil.writeToCache(attemptsKey, String.valueOf(attempts), 2L);

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

    private String extractEmailFromToken(String accessToken,Language language) {
        try {
            Claims claims = jwtService.parseToken(accessToken);
            return claims.getSubject();
        } catch (Exception e) {
            throw new UserOperationException(
                    getTranslationByLanguage(ACCESS_TOKEN_INVALID_CODE, language),
                    ACCESS_TOKEN_INVALID);

    }
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

    public SignUpResponse retryOtp(String email,
                         Language language) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserOperationException(
                        getTranslationByLanguage(EMAIL_NOT_REGISTERED_CODE, language),
                        EMAIL_NOT_REGISTERED));

        String otp = generateOTP();
        String message = String.format("OTP: %s", otp);
        MailRequest mailDto = userMapper.emailToMailRequest(email, message);
        emailService.sendEmail(mailDto, language);
        cacheUtil.writeToCache(email + ":" + otp, "RETRY_OTP", 1L);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

            return new SignUpResponse(accessToken, refreshToken);
    }

    public SignUpResponse login(LoginRequest loginRequest, Language language) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        String attemptsKey = "login_attempts:" + email;
        String totalAttemptsKey = "total_login_attempts:" + email;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserOperationException(
                        getTranslationByLanguage(USER_NOT_FOUND_CODE, language),
                        USER_NOT_FOUND
                ));

            int attempts = Optional.ofNullable(cacheUtil.onlyReadWithKeyFromCache(attemptsKey))
                    .map(Object::toString)
                    .map(Integer::parseInt)
                    .orElse(0) + 1;

            int totalAttempts = Optional.ofNullable(cacheUtil.onlyReadWithKeyFromCache(totalAttemptsKey))
                    .map(Object::toString)
                    .map(Integer::parseInt)
                    .orElse(0) + 1;


            if (attempts >= 4) {
                throw new UserOperationException(
                        getTranslationByLanguage(ACCOUNT_BLOCKED_5MIN_CODE, language),
                        ACCOUNT_BLOCKED_5MIN
                );
            }
            if (totalAttempts >= 5) {
                throw new UserOperationException(
                        getTranslationByLanguage(ACCOUNT_BLOCKED_NO_LIMIT_CODE, language),
                        ACCOUNT_BLOCKED_NO_LIMIT
                );
            }
            cacheUtil.writeToCache(attemptsKey, String.valueOf(attempts), 5L);
            cacheUtil.writeToCacheNoLimit(totalAttemptsKey, String.valueOf(totalAttempts));

            if (!securityConfig.passwordEncoder().matches(password, user.getPassword())) {
            throw new UserOperationException(
                    getTranslationByLanguage(INCORRECT_PASSWORD_CODE, language),
                    INCORRECT_PASSWORD
            );
        }

        cacheUtil.deleteFromCache(attemptsKey);
        cacheUtil.deleteFromCache(totalAttemptsKey);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new SignUpResponse(accessToken, refreshToken);
    }

}

