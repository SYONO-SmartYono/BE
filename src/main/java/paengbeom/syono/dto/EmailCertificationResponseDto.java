package paengbeom.syono.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class EmailCertificationResponseDto {
    private String email;
    private String certificationNumber;
}
