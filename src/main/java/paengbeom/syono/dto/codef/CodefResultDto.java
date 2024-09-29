package paengbeom.syono.dto.codef;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodefResultDto {
    private String code;
    private String extraMessage;
    private String message;
    private String transactionId;
}
