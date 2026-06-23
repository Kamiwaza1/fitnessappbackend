package com.zeki.demo.common.config;

import com.zeki.demo.exerciselibrary.config.ExerciseLibraryCacheNames;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    RedisCacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            @Value("${cache.exercise-library.ttl:PT6H}") Duration exerciseLibraryTtl
    ) {
        RedisCacheConfiguration cacheConfiguration = baseCacheConfiguration(exerciseLibraryTtl)
                .serializeValuesWith(jsonValueSerializer(Object.class));
        RedisCacheConfiguration exerciseLibraryCacheConfiguration = baseCacheConfiguration(exerciseLibraryTtl);
        TypeFactory typeFactory = TypeFactory.createDefaultInstance();

        Map<String, RedisCacheConfiguration> exerciseLibraryCaches = Map.of(
                ExerciseLibraryCacheNames.SEARCH, exerciseLibraryCacheConfiguration.serializeValuesWith(jsonValueSerializer(ExerciseLibrary.class)),
                ExerciseLibraryCacheNames.ITEM, exerciseLibraryCacheConfiguration.serializeValuesWith(jsonValueSerializer(ExerciseLibraryItem.class)),
                ExerciseLibraryCacheNames.MUSCLES, exerciseLibraryCacheConfiguration.serializeValuesWith(jsonValueSerializer(typeFactory.constructCollectionType(List.class, ExerciseMuscle.class))),
                ExerciseLibraryCacheNames.EQUIPMENT, exerciseLibraryCacheConfiguration.serializeValuesWith(jsonValueSerializer(typeFactory.constructCollectionType(List.class, ExerciseReference.class))),
                ExerciseLibraryCacheNames.CATEGORIES, exerciseLibraryCacheConfiguration.serializeValuesWith(jsonValueSerializer(typeFactory.constructCollectionType(List.class, ExerciseReference.class)))
        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .withInitialCacheConfigurations(exerciseLibraryCaches)
                .build();
    }

    private RedisCacheConfiguration baseCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    }

    private RedisSerializationContext.SerializationPair<?> jsonValueSerializer(Class<?> type) {
        return RedisSerializationContext.SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(type));
    }

    private RedisSerializationContext.SerializationPair<?> jsonValueSerializer(JavaType type) {
        return RedisSerializationContext.SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(type));
    }
}
