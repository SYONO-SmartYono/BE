package paengbeom.syono.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomException extends RuntimeException {

    private final String code;
    private final String message;

    public CustomException(String code, String message) {
        super(message, null, false, false);
        this.code = code;
        this.message = message;
    }
}
