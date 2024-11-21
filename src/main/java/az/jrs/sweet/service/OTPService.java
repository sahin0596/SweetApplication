package az.jrs.sweet.service;
import az.jrs.sweet.mapstruck.UserMapper;
import az.jrs.sweet.model.entity.OTP;
import az.jrs.sweet.model.entity.User;
import az.jrs.sweet.repository.OTPRepository;
import az.jrs.sweet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final OTPRepository otpRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }

    public void saveOTP(String email, String otp) {
        User userId = userRepository.findByEmail(email);
            OTP otpEntity = new OTP();
            otpEntity.setUser(userId);
            otpEntity.setOtp(otp);
            otpEntity.setCreated(java.time.LocalDateTime.now());
            otpRepository.save(otpEntity);
        }
}
