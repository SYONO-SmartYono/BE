package paengbeom.syono.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CodefAccountListDto {
    private String code;
    private String businessType;
    private String id;
    private String password;
}
