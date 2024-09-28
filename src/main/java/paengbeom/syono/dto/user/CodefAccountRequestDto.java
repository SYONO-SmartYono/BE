package paengbeom.syono.dto.user;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CodefAccountRequestDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String id;
    @NotEmpty
    private String password;
}
