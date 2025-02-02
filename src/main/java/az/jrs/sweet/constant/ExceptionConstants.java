package az.jrs.sweet.constant;

public interface ExceptionConstants {

    String EMAIL_ALREADY_EXISTS = "Email Has Already Exists";
    String OTP_INCORRECT = "This OTP incorrect";
    String MAX_ATTEMPTS_EXCEEDED = "Max Attempts Exceeded";
    String EMAIL_NOT_VERIFIED = "Email Not Verified";
    String USER_NOT_FOUND = "User Not Found";
    String EMAIL_NOT_ACTIVE = "Email Not Active";
    String EMAIL_NOT_REGISTERED = "Email Not Registered";
    String INCORRECT_PASSWORD = "Incorrect Password";
    String ACCOUNT_BLOCKED_5MIN = "Account Blocked 5 Min";
    String ACCOUNT_BLOCKED_NO_LIMIT = "Account Blocked No Limit";
    String MISSING_IDEMPOTENCY_KEY = "Missing Idempotency Key";
    String DUPLICATE_REQUEST = "Duplicate Request";
    String ACCESS_TOKEN_INVALID = "Access Token Invalid";
}
