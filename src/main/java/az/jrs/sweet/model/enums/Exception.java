package az.jrs.sweet.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Exception {
    EMAIL_NOT_ACTIVE_CODE(
            "EMAIL_NOT_ACTIVE",
            "Bu e-poçt ünvanı aktiv deyil. Zəhmət olmasa aktiv e-poçt ünvanı istifadə edin.",
            "This email is not active. Please use an active email address.",
            "Этот адрес электронной почты не активен. Пожалуйста, используйте активный адрес электронной почты."
    ),
    EMAIL_ALREADY_EXISTS_CODE(
            "EMAIL_ALREADY_EXISTS",
            "Bu e-poçt ünvanı artıq qeydiyyatdan keçib.",
            "This email is already registered.",
            "Этот адрес электронной почты уже зарегистрирован."
    ),
    MAX_ATTEMPTS_EXCEEDED_CODE(
            "MAX_ATTEMPTS_EXCEEDED",
            "Siz artıq 3-dəfə cəhd etmisiniz.",
            "You have already attempted 3 times.",
            "Вы уже сделали 3 попытки."
    ),
    TOKEN_EMAIL_MISMATCH_CODE(
            "TOKEN_EMAIL_MISMATCH",
            "Token email ünvanı təqdim olunan email ilə uyğun gəlmir.",
            "Token email does not match the provided email.",
            "Токен не соответствует предоставленному email."
    ),

    OTP_INCORRECT_CODE(
            "OTP_INCORRECT",
            "OTP düzgün deyil.",
            "OTP is incorrect.",
            "OTP неверен."
    ),
    EMAIL_NOT_VERIFIED_CODE(
            "EMAIL_NOT_VERIFIED",
                    "Email təsdiqlənməyib.",
                    "Email is not verified.",
                    "Электронная почта не подтверждена."
    ),
    USER_NOT_FOUND_CODE(
            "USER_NOT_FOUND",
            "İstifadəçi tapılmadı.",
            "User not found.",
            "Пользователь не найден."
    ),
    TOKEN_EXPIRED_CODE(
            "TOKEN_EXPIRED",
                    "Tokenin müddəti bitib.",
                    "Token has expired.",
                    "Срок действия токена истек."
    );
    private final String code;
    private final String translationAz;
    private final String translationEn;
    private final String translationRu;


    public static String getTranslationByLanguage(Exception exception, Language language) {
        return switch (language) {
            case ENG -> exception.getTranslationEn();
            case RU -> exception.getTranslationRu();
            default -> exception.getTranslationAz();
        };
    }
}