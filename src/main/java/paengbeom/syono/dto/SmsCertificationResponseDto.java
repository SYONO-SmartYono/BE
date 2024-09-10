package paengbeom.syono.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class SmsCertificationResponseDto {
    private String phoneNumber;
    private String certificationNumber;
}
