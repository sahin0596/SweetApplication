package az.jrs.sweet.controller;
import az.jrs.sweet.dto.request.SignUpRequest;
import az.jrs.sweet.dto.request.VerifyOtpRequest;
import az.jrs.sweet.model.enums.Language;
import az.jrs.sweet.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static az.jrs.sweet.constant.Headers.LANGUAGE;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class RegistrationController {

    private final RegistrationService registrationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest,
                                         @RequestHeader(value = LANGUAGE,defaultValue = "AZE") Language language ) {
        registrationService.signUp(signUpRequest,language);
        return ResponseEntity.ok("OTP sent to email. Please verify to complete registration.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest,
                                            @RequestHeader(value = LANGUAGE,defaultValue = "AZE") Language language ) {
        {
            registrationService.verifyOtp(verifyOtpRequest,language);
            return ResponseEntity.ok("Otp is succecfully verify");
        }
    }

}