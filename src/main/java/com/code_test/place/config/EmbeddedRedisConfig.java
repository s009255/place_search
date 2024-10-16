package com.test.place.config;


import com.sun.jdi.InternalException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig {


    private final int redisPort = 6379;
    private RedisServer redisServer;

    public EmbeddedRedisConfig() {
    }

    private boolean isArmMac() {
        return Objects.equals(System.getProperty("os.arch"), "aarch64")
            && Objects.equals(System.getProperty("os.name"), "Mac OS X");
    }

    private File getRedisFileForArcMac() {
        try {
            return new ClassPathResource("binary/redis-server-7.2.3").getFile();
        } catch (Exception e) {
            throw new InternalException(e.getMessage());
        }
    }

    @PostConstruct
    public void startRedis() {

        if (isArmMac()) {
            redisServer = new RedisServer(getRedisFileForArcMac(), redisPort);
        } else {
            redisServer = RedisServer.builder()
                .port(redisPort)
                .build();
        }

        try {
            redisServer.start();
        } catch (Exception e) {
            throw new InternalException(e.getMessage());
        }
    }


    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory("localhost", redisPort);
        factory.afterPropertiesSet(); // Ensure the factory is properly initialized
        return factory;
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> keywordRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }


    @Bean
    public RedisTemplate<String, Object> cacheRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
