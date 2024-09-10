package paengbeom.syono.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String HOST;

    @Value("${spring.data.redis.port}")
    private int PORT;

    public RedisConnectionFactory redisMailConnectionFactory() {
        return new LettuceConnectionFactory(HOST, PORT);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisMailConnectionFactory());
        return redisTemplate;
    }
}
