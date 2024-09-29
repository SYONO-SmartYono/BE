package paengbeom.syono.dto.sms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SmSCertificationRequestDto {

    @NotEmpty
    @Pattern(regexp = "^010[0-9]{8}$", message = "전화번호는 010으로 시작하고, 11자리여야 합니다.")
    private String phone;
}

