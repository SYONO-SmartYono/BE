package paengbeom.syono.dto.codef;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CodefApiResultDto {
    private String code;
    private String extraMessage;
    private String message;
    private String transactionId;
}
