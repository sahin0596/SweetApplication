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
    EMAIL_NOT_REGISTERED_CODE(
            "EMAIL_NOT_REGISTERED",
            "Bu e-poçt ünvanı qeydiyyatdan keçməyib.",
            "This email is not registered.",
            "Этот адрес электронной почты не зарегистрирован."
    )
    ,
    MAX_ATTEMPTS_EXCEEDED_CODE(
            "MAX_ATTEMPTS_EXCEEDED",
            "Siz artıq 3-dəfə cəhd etmisiniz.",
            "You have already attempted 3 times.",
            "Вы уже сделали 3 попытки."
    ),
    ACCESS_TOKEN_INVALID_CODE(
            "ACCESS_TOKEN_INVALID",
            "Bu giriş tokeni keçərli deyil.",
            "This access token is invalid.",
            "Этот токен доступа недействителен."
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
    INCORRECT_PASSWORD_CODE(
            "INCORRECT_PASSWORD",
            "Şifrə yanlışdır.",
            "Incorrect password.",
            "Неверный пароль."
    ),
    ACCOUNT_BLOCKED_CODE(
            "ACCOUNT_BLOCKED",
            "Hesabınız müvəqqəti bloklanıb.",
            "Your account is temporarily blocked.",
            "Ваш аккаунт временно заблокирован."
    ),

    ACCOUNT_BLOCKED_5MIN_CODE(
            "ACCOUNT_BLOCKED_5MIN",
            "3 dəfə səhv etdiniz. Hesabınız 5 dəqiqəlik bloklanıb.",
            "You have entered the wrong password 3 times. Your account is blocked for 5 minutes.",
            "Вы ввели неправильный пароль 3 раза. Ваш аккаунт заблокирован на 5 минут."
    ),
    ACCOUNT_BLOCKED_NO_LIMIT_CODE(
            "ACCOUNT_BLOCKED_NO_LIMIT",
            "Hesabınız bloklanıb, zəhmət olmasa bizimlə əlaqə saxlayın.",
            "Your account is blocked, please contact support.",
            "Ваша учетная запись заблокирована, пожалуйста, свяжитесь с поддержкой."
    ),
    MISSING_IDEMPOTENCY_KEY_CODE(
            "MISSING_IDEMPOTENCY_KEY",
            "Idempotency-Key tələb olunur!",
            "Idempotency-Key is required!",
            "Требуется Idempotency-Key"
    ),
    DUPLICATE_REQUEST_CODE(
            "DUPLICATE_REQUEST",
            "Eyni idempotent açar ilə təkrarlanan sorğu aşkar edildi.",
            "Duplicate request detected with the same idempotency key.",
            "Обнаружен повторяющийся запрос с тем же ключом идемпотентности."
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