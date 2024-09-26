package paengbeom.syono.dto.user;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CodefAccountRequestDto {
    @NotEmpty
    @Pattern(regexp = "^[0-9]{4}$")
    private String organization;
    @NotEmpty
    @Pattern(regexp = "^[A-Z]{2}$")
    private String businessType;
    @NotEmpty
    private String id;
    @NotEmpty
    private String password;
}
