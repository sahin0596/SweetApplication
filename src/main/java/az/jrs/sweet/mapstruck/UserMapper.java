package az.jrs.sweet.mapstruck;
import az.jrs.sweet.dto.request.MailRequest;
import az.jrs.sweet.dto.request.SignUpRequest;
import az.jrs.sweet.model.entity.OTP;
import az.jrs.sweet.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);


    @Mapping(target = "to", source = "signUpRequest.email")
    @Mapping(target = "cc", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "subject", constant = "SWEET APPLICATION")
    @Mapping(target = "message", source = "message")
    MailRequest signUpRequestToMailRequest(SignUpRequest signUpRequest, String message);


    User signUpRequestToUser(SignUpRequest signUpRequest);


    @Mapping(target = "user", source = "user")
    @Mapping(target = "otp", source = "otp")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    OTP mapToOTP(User user, String otp);
}
