package com.zeki.demo.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @SuppressWarnings("removal")
    RedisCacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper,
            @Value("${cache.exercise-library.ttl:PT6H}") Duration exerciseLibraryTtl
    ) {
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper.copy());
                //convert java objects to/from JSON for storing redis values.
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(exerciseLibraryTtl)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}
