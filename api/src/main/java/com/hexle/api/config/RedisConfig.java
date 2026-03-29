package com.hexle.api.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.hexle.api.model.GameState;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, GameState> gameStateRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, GameState> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        RedisSerializer<GameState> serializer = new RedisSerializer<>() {
            @Override
            public byte[] serialize(GameState value) throws SerializationException {
                if (value == null) return null;
                try {
                    return mapper.writeValueAsBytes(value);
                } catch (Exception e) {
                    throw new SerializationException("Cannot serialize GameState", e);
                }
            }

            @Override
            public GameState deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) return null;
                try {
                    return mapper.readValue(bytes, GameState.class);
                } catch (Exception e) {
                    throw new SerializationException("Cannot deserialize GameState", e);
                }
            }
        };

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
