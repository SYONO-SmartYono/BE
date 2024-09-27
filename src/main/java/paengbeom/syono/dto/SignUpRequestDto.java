package paengbeom.syono.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class SignUpRequestDto {

    private String email;
    private String password;
    private String phone;
    private String connectedId;
}
