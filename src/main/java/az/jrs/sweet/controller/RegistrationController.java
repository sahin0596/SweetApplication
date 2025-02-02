package az.jrs.sweet.controller;

import az.jrs.sweet.dto.request.LoginRequest;
import az.jrs.sweet.dto.request.SignUpRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.dto.response.SignUpResponse;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static az.jrs.sweet.constant.Headers.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class RegistrationController {

    private final RegistrationService registrationService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/retry-otp")
    public SignUpResponse retryOtp(@RequestParam String email,
                         @RequestHeader(value = LANGUAGE, defaultValue = "AZE") Language language) {
       return registrationService.retryOtp(email, language);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    public SignUpResponse signUp(@RequestBody SignUpRequest signUpRequest,
                                 @RequestHeader(value = IDEMPOTENCY_KEY) String idempotencyKey,
                                 @RequestHeader(value = LANGUAGE, defaultValue = "AZE") Language language) {
        return registrationService.signUp(signUpRequest, idempotencyKey, language);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/verify-otp")
    public void verifyOtp(@RequestParam String otp,
                          @RequestHeader(value = AUTHORIZATION) String accessToken,
                          @RequestHeader(value = LANGUAGE, defaultValue = "AZE") Language language) {
        {
            registrationService.verifyOtp(otp, accessToken, language);
        }
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public SignUpResponse login(@RequestBody LoginRequest loginRequest,
                               @RequestHeader(value = LANGUAGE, defaultValue = "AZE") Language language) {
        return registrationService.login(loginRequest, language);
    }
}

