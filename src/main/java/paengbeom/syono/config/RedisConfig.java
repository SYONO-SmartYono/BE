package paengbeom.syono.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String HOST;

    @Value("${spring.data.redis.port}")
    private int PORT;

    @Value("${spring.data.redis.password}")
    private String PASSWORD;

    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactoryDB0() {
        return createConnectionFactoryWith(0);
    }

    @Bean
    public StringRedisTemplate redisTemplateDB0(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    @Qualifier("2")
    public LettuceConnectionFactory redisConnectionFactoryDB1() {
        return createConnectionFactoryWith(1);
    }

    @Bean
    public StringRedisTemplate redisTemplateDB1(@Qualifier("2") RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    private LettuceConnectionFactory createConnectionFactoryWith(int index) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(HOST);
        redisStandaloneConfiguration.setPort(PORT);
        redisStandaloneConfiguration.setPassword(PASSWORD);
        redisStandaloneConfiguration.setDatabase(index);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}
