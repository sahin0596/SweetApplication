package az.jrs.sweet.service;

import az.jrs.sweet.config.SecurityConfig;
import az.jrs.sweet.constant.ExceptionConstants;
import az.jrs.sweet.dto.request.MailRequest;
import az.jrs.sweet.dto.request.SignInRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.dto.response.SignInResponse;
import az.jrs.sweet.dto.response.SignUpResponse;
import az.jrs.sweet.exception.UserOperationException;
import az.jrs.sweet.model.entity.User;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.repository.UserRepository;
import az.jrs.sweet.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import static az.jrs.sweet.constant.ExceptionConstants.*;
import static az.jrs.sweet.model.enums.Exception.*;
import static az.jrs.sweet.model.enums.Exception.INVALID_CREDENTIALS_CODE;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CacheUtil cacheUtil;
    private final JwtService jwtService;
    private final SecurityConfig securityConfig;

    /**
     * Login için OTP gönderme işlemi
     */
    public void requestOtpForLogin(SignInRequest signInRequest, Language language) {
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UserOperationException(
                        getTranslationByLanguage(USER_NOT_FOUND_CODE, language),
                        USER_NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getIsRegistered())) {
            throw new UserOperationException(
                    getTranslationByLanguage(EMAIL_NOT_VERIFIED_CODE, language),
                    EMAIL_NOT_VERIFIED);
        }

        if (!securityConfig.passwordEncoder().matches(signInRequest.getPassword(), user.getPassword())) {
            throw new UserOperationException(
                    getTranslationByLanguage(INVALID_CREDENTIALS_CODE, language),
                    ExceptionConstants.INVALID_CREDENTIALS_CODE);
        }

        String otp = generateOTP();
        String message = String.format("Your OTP for login is: %s", otp);

        List<String> ccList = List.of(); // Veya istediğiniz CC e-posta adreslerini buraya ekleyebilirsiniz.

        MailRequest mailDto = new MailRequest(signInRequest.getEmail(), ccList, "Login OTP", message);

        emailService.sendEmail(mailDto, language);

        cacheUtil.writeToCache(signInRequest.getEmail() + ":" + otp, "LOGIN_OTP", 2L); // 2 dakika geçerlilik
    }

    /**
     * OTP doğrulama ve JWT token oluşturma işlemi
     */
    public SignInResponse verifyOtpForLogin(VerifyOtpRequest verifyOtpRequest, Language language) {
        String email = verifyOtpRequest.getEmail();
        String otp = verifyOtpRequest.getOtp();

        boolean isCorrectOtp = validateOtp(email, otp);
        if (!isCorrectOtp) {
            throw new UserOperationException(
                    getTranslationByLanguage(OTP_INCORRECT_CODE, language),
                    OTP_INCORRECT);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserOperationException(
                        getTranslationByLanguage(USER_NOT_FOUND_CODE, language),
                        USER_NOT_FOUND));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        cacheUtil.deleteFromCache(email + ":" + otp);

        return new SignInResponse(accessToken, refreshToken);
    }

    /**
     * OTP doğrulama işlemi
     */
    private boolean validateOtp(String email, String otp) {
        String otpKey = email + ":" + otp;
        return cacheUtil.onlyReadWithKeyFromCache(otpKey) != null;
    }

    /**
     * OTP üretme metodu
     */
    private String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000); // 6 haneli OTP
        return String.valueOf(otp);
    }
}







