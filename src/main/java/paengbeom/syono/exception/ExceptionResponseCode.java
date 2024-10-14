package paengbeom.syono.exception;

import lombok.Getter;

@Getter
public enum ExceptionResponseCode {

    DUPLICATED_EMAIL("email-1", "이미 등록되어 있는 이메일입니다."),
    INVALID_EMAIL("email-2", "유효하지 않은 이메일이거나 인증 코드가 만료되었습니다."),
    INVALID_EMAIL_CODE("email-3", "유효하지 않은 인증 코드입니다."),
    EMAIL_SEND_FAILURE("email-4", "이메일 전송에 실패하였습니다."),


    DUPLICATED_PHONE("sms-1", "이미 등록되어 있는 핸드폰입니다."),
    INSUFFICIENT_BALANCE("sms-2", "CoolSMS 잔액이 부족합니다."),
    INVALID_PHONE("sms-3", "유효하지 않은 번호거나 인증 코드가 만료되었습니다."),
    INVALID_PHONE_CODE("sms-4", "유효하지 않은 인증 코드입니다."),

    UNVERIFIED_EMAIL("user-1", "인증되지 않은 이메일입니다."),
    UNVERIFIED_PHONE("user-2", "인증되지 않은 핸드폰 번호입니다."),

    NOT_EXISTED_COMPANY("codef-1", "지원하지 않는 회사입니다."),
    ENCRYPTION_FAILURE("codef-2", "암호화에 실패했습니다."),
    NOT_EXISTED_EMAIL("codef-3", "존재하지 않는 회원입니다."),
    ACCOUNT_CREATION_FAILURE("codef-4", "ConnectedId 생성에 실패했습니다."),

    NOT_ALLOWED("d-1", "권한이 업습니다."),
    NOT_ADMIN("d-2", "관리자가 아닙니다."),

    JSON_PROCESSING_FAILURE("json-1", "JSON 처리 중 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ExceptionResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
