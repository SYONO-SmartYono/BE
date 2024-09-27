package paengbeom.syono.exception;

import lombok.Getter;

@Getter
public enum ExceptionResponseCode {

    DUPLICATED_EMAIL("e-1", "이미 등록되어 있는 이메일입니다."),
    INVALID_EMAIL("e-2", "유효하지 않은 이메일이거나 인증 코드가 만료되었습니다."),
    INVALID_EMAIL_CODE("e-3", "유효하지 않은 인증 코드입니다."),
    EMAIL_SEND_FAILURE("e-4", "이메일 전송에 실패하였습니다."),


    DUPLICATED_PHONE("s-1", "이미 등록되어 있는 핸드폰입니다."),
    INSUFFICIENT_BALANCE("s-2", "CoolSMS 잔액이 부족합니다."),
    INVALID_PHONE("s-3", "유효하지 않은 번호거나 인증 코드가 만료되었습니다."),
    INVALID_PHONE_CODE("s-4", "유효하지 않은 인증 코드입니다."),

    NOT_EXISTED_EMAIL("u-2", "존재하지 않는 회원입니다."),
    NOT_ALLOWED("d-1", "권한이 업습니다."),
    NOT_ADMIN("d-2", "관리자가 아닙니다.");

    private final String code;
    private final String message;

    ExceptionResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
