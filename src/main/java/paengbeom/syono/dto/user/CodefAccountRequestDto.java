package paengbeom.syono.dto.user;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CodefAccountRequestDto {
    private String organization;
    private String businessType;
    private String id;
    private String password;
}
