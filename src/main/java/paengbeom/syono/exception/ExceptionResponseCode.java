package paengbeom.syono.exception;

public enum ExceptionResponseCode {

    INVALID_EMAIL_CODE("e-1", "유효화지 않은 이메일 인증 코드."),
    NOT_EXISTED_EMAIL("u-1", "존재하지 않는 회원입니다."),
    DUPLICATED_EMAIL("u-2", "이미 등록되어 있는 이메일입니다."),
    NOT_ALLOWED("s-1", "권한이 업습니다."),
    NOT_ADMIN("s-2", "관리자가 아닙니다.");

    private final String code;
    private final String message;

    ExceptionResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
