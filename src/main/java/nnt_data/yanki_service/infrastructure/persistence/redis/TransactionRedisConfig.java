package nnt_data.yanki_service.infrastructure.persistence.redis;

import nnt_data.yanki_service.entity.TransactionYanki;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class TransactionRedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, TransactionYanki> transactionRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<TransactionYanki> serializer =
                new Jackson2JsonRedisSerializer<>(TransactionYanki.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, TransactionYanki> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, TransactionYanki> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}