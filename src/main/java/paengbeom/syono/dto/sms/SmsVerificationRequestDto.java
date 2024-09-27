package paengbeom.syono.dto.sms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SmsVerificationRequestDto {
    @NotEmpty
    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;
    @NotEmpty
    @Pattern(regexp = "^[0-9]{6}$")
    private String certificationCode;
}
