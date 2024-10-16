package com.code_test.place.service;


import com.code_test.place.dto.Keyword;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KeywordService {

    private static final String KEYWORDS_KEY = "keywords";
    private final RedisTemplate<String, String> keywordRedisTemplate;

    public KeywordService(RedisTemplate<String, String> redisTemplate) {
        this.keywordRedisTemplate = redisTemplate;
    }

    public void addKeyword(String keyword) {
        keywordRedisTemplate.opsForZSet().incrementScore(KEYWORDS_KEY, keyword, 1);
    }

    public List<Keyword> getTopKeywords() {
        return keywordRedisTemplate.opsForZSet()
            .reverseRangeWithScores("keywords", 0, 9)
            .stream()
            .map(tuple -> Keyword.builder()
                .name(tuple.getValue())
                .count(tuple.getScore().intValue())
                .build())
            .collect(Collectors.toList());
    }

}

