package paengbeom.syono.dto.codef;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
public class CodefgetCardListDto {
    private String resCardNo;
    private String resCardName;
    private String resImageLink;
}
