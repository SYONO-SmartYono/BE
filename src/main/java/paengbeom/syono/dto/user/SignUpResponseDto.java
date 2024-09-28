package paengbeom.syono.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {
    private String email;
    private String phone;
    private String nickname;
    private String role;
    private String profileImg;
}
