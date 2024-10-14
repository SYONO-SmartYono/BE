package paengbeom.syono.dto.codef;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CodefApiResponseDto<T> {
    private CodefApiResultDto result;
    private T data;
}
