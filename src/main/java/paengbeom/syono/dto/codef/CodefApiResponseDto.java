package paengbeom.syono.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodefApiResponseDto<T> {
    private CodefResultDto result;
    private T data;
}
