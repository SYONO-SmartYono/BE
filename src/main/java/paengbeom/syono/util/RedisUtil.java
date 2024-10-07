package paengbeom.syono.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final StringRedisTemplate template;

    /**
     * Redis에서 해당 키에 대한 데이터를 가져오는 메서드
     *
     * @param key Redis에서 데이터를 조회할 키
     * @return 해당 키에 저장된 데이터 값 (문자열)
     */
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * Redis에 해당 키가 존재하는지 확인하는 메서드
     *
     * @param key 존재 여부를 확인할 키
     * @return 해당 키가 존재하면 true, 없으면 false
     */
    public boolean isExistData(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    /**
     * Redis에 데이터를 저장하고 유효 기간을 설정하는 메서드
     *
     * @param key      저장할 데이터의 키
     * @param value    저장할 데이터 값
     * @param duration 데이터의 유효 기간 (초 단위)
     */
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    /**
     * Redis에서 해당 키의 데이터를 삭제하는 메서드
     *
     * @param key 삭제할 데이터의 키
     */
    public void delData(String key) {
        template.delete(key);
    }
}
