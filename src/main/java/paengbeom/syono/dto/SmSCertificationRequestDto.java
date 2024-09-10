package paengbeom.syono.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmSCertificationRequestDto {

    @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하고, 11자리여야 합니다.")
    private String phoneNumber;
}
