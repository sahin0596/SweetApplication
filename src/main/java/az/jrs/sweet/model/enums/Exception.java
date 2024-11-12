package az.jrs.sweet.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Exception {
    EMAIL_ALREADY_EXISTS(
            "EMAIL_ALREADY_EXISTS",
            "Bu e-poçt ünvanı artıq qeydiyyatdan keçib.",
            "This email is already registered.",
            "Этот адрес электронной почты уже зарегистрирован."
    ),
    OTP_NOT_FOUND(
            "OTP_NOT_FOUND",
            "OTP tapılmadı.",
            "OTP is not found.",
            "Этот OTP не найден."
    ),
    OTP_INCORRECT(
            "OTP_INCORRECT",
            "OTP düzgün deyil.",
            "OTP is incorrect.",
            "OTP неверен."
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