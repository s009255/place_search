package com.code_test.place.component.aspect;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.code_test.place.component.aspect.annotation.RedisCache;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final ObjectMapper objectMapper;

    @Around(value = "execution(* *(..)) && @annotation(redisCache)")
    public Object getMono(ProceedingJoinPoint joinPoint, RedisCache redisCache) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        List<String> parametersName = List.of(methodSignature.getParameterNames());
        String cacheKey = createCacheKey(redisCache.prefix(), redisCache.params(), args,
            parametersName);

        try {
            Object cacheResult = cacheRedisTemplate.opsForValue().get(cacheKey);
            if (ObjectUtils.isNotEmpty(cacheResult)) {
                if (cacheResult instanceof List) {
                    return Mono.just(
                        objectMapper.convertValue(cacheResult, new TypeReference<List<?>>() {
                            @Override
                            public Type getType() {
                                return TypeFactory.defaultInstance()
                                    .constructCollectionType(List.class, redisCache.type());
                            }
                        }));
                } else {
                    return Mono.just(objectMapper.convertValue(cacheResult, redisCache.type()));
                }
            }
        } catch (Exception e) {
            log.error("[Error] Redis aspect - cache Get Error: {}", e.getMessage(), e);
        }
        Object result = joinPoint.proceed();

        ((Mono<?>) result).subscribe(value -> {
            try {
                cacheRedisTemplate.opsForValue()
                    .set(cacheKey, value, redisCache.expireMinutes(), TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("[Error] Redis aspect - cache Set Error: {}", e.getMessage(), e);
            }
        });

        return result;
    }


    private String createCacheKey(String prefix, String[] params, Object[] args,
        List<String> parametersName) {
        if (params.length == 0) {
            return prefix;
        }
        StringBuilder keyBuilder = new StringBuilder(prefix);
        for (String param : params) {
            Object value = args[parametersName.indexOf(param)];
            if (ObjectUtils.isNotEmpty(value)) {
                keyBuilder.append(value.toString().concat(":"));
            }

        }
        return prefix + ":" + StringUtils.removeEnd(keyBuilder.toString(), ":");
    }
}
