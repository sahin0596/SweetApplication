package az.jrs.sweet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyOtpRequest {
    String otp;
}
