package paengbeom.syono.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;

    public ErrorResult(String message) {
        this.message = message;
    }

    public ErrorResult(int code, String message) {
        this.code = String.valueOf(code);
        this.message = message;
    }
}
