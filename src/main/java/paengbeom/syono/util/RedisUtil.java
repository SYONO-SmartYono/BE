package paengbeom.syono.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import paengbeom.syono.dto.codef.CodefgetCardListDto;
import paengbeom.syono.exception.CustomException;

import java.time.Duration;
import java.util.List;

import static paengbeom.syono.exception.ExceptionResponseCode.JSON_PROCESSING_FAILURE;

@Component
public class RedisUtil {

    private final StringRedisTemplate templateDB0;
    private final StringRedisTemplate templateDB1;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisUtil(@Qualifier("redisTemplateDB0") StringRedisTemplate templateDB0,
                     @Qualifier("redisTemplateDB1") StringRedisTemplate templateDB1,
                     ObjectMapper objectMapper) {
        this.templateDB0 = templateDB0;
        this.templateDB1 = templateDB1;
        this.objectMapper = objectMapper;
    }

    /**
     * Redis에서 해당 키에 대한 데이터를 가져오는 메서드
     *
     * @param key Redis에서 데이터를 조회할 키
     * @return 해당 키에 저장된 데이터 값 (문자열)
     */
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = templateDB0.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * Redis에 해당 키가 존재하는지 확인하는 메서드
     *
     * @param key 존재 여부를 확인할 키
     * @return 해당 키가 존재하면 true, 없으면 false
     */
    public boolean isExistData(String key) {
        return Boolean.TRUE.equals(templateDB0.hasKey(key));
    }

    /**
     * Redis에 데이터를 저장하고 유효 기간을 설정하는 메서드
     *
     * @param key      저장할 데이터의 키
     * @param value    저장할 데이터 값
     * @param duration 데이터의 유효 기간 (초 단위)
     */
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = templateDB0.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    /**
     * Redis에서 해당 키의 데이터를 삭제하는 메서드
     *
     * @param key 삭제할 데이터의 키
     */
    public void delData(String key) {
        templateDB0.delete(key);
    }

    public void saveCardList(String email, List<CodefgetCardListDto> cardList) {
        String cardListJson = null;
        try {
            cardListJson = objectMapper.writeValueAsString(cardList);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESSING_FAILURE.getCode(), JSON_PROCESSING_FAILURE.getMessage());
        }
        templateDB1.opsForValue().set(email, cardListJson);
    }

    public List<CodefgetCardListDto> getCardList(String email) {
        String cardListJson = templateDB1.opsForValue().get(email);
        try {
            return objectMapper.readValue(cardListJson, new TypeReference<List<CodefgetCardListDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESSING_FAILURE.getCode(), JSON_PROCESSING_FAILURE.getMessage());

        }
    }
}
