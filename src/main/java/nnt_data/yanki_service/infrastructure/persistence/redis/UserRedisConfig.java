package nnt_data.yanki_service.infrastructure.persistence.redis;


import nnt_data.yanki_service.entity.UserYanki;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class UserRedisConfig {
    @Bean
    @Primary
    public ReactiveRedisTemplate<String, UserYanki> userRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserYanki> serializer =
                new Jackson2JsonRedisSerializer<>(UserYanki.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, UserYanki> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, UserYanki> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}