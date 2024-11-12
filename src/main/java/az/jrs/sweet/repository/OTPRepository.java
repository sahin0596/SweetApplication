package az.jrs.sweet.repository;

import az.jrs.sweet.model.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {


    boolean existsByUserIdAndIsRegisteredTrue(Long userId);

    @Query(value = "SELECT * FROM otps o WHERE o.user_id = :userId AND o.is_registered = false AND o.retry_count < 3 ORDER BY o.created DESC LIMIT 1", nativeQuery = true)
    Optional<OTP> findLatestOtpByUserIdAndConditions(@Param("userId") Long userId);


}
