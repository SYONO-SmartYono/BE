package paengbeom.syono.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCertificationRequestDto {

    @Email(message = "이메일 형식에 맞게 입력해 주세요.")
    @NotEmpty(message = "이메일을 입력은 필수입니다.")
    private String email;
}
