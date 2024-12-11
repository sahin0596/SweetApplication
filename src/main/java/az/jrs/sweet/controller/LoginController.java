package az.jrs.sweet.controller;

import az.jrs.sweet.dto.request.SignInRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.dto.response.SignInResponse;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/signIn-otp")
    public ResponseEntity<String> requestOtp(@RequestBody SignInRequest signInRequest,
                                             @RequestHeader(value = "Language", defaultValue = "AZE") Language language) {
        loginService.requestOtpForLogin(signInRequest, language);
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<SignInResponse> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest,
                                                    @RequestHeader(value = "Language", defaultValue = "AZE") Language language) {
        SignInResponse signInResponse = loginService.verifyOtpForLogin(verifyOtpRequest, language);
        return ResponseEntity.ok(signInResponse);
    }
}
