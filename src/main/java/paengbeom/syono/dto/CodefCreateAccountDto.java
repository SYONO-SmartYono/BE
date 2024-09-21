package paengbeom.syono.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CodefCreateAccountDto {
    private String connectedId;
    private List<Map<String, String>> successList;
    private List<Map<String, String>> errorList;
}
