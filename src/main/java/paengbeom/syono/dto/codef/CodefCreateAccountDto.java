package paengbeom.syono.dto.codef;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
public class CodefCreateAccountDto {
    private String connectedId;
    private List<Map<String, String>> successList;
    private List<Map<String, String>> errorList;
}
