package com.code_test.place.service;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.code_test.place.dto.Keyword;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;


class KeywordServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private KeywordService keywordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    @DisplayName("Test addKeyword and getTopKeywords")
    void testAddKeywordAndGetTopKeywords() {
        String keyword1 = "testKeyword1";
        String keyword2 = "testKeyword2";

        // Mock incrementScore
        when(zSetOperations.incrementScore(anyString(), anyString(), anyDouble())).thenReturn(1.0);

        // Add keywords
        keywordService.addKeyword(keyword1);
        keywordService.addKeyword(keyword2);
        keywordService.addKeyword(keyword2);

        // Mock reverseRangeWithScores
        Set<TypedTuple<String>> mockResult = Stream.of(
            new MockTypedTuple(keyword1, 1.0),
            new MockTypedTuple(keyword2, 2.0)
        ).collect(Collectors.toSet());

        when(zSetOperations.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(
            mockResult);

        // Get top keywords
        List<Keyword> topKeywords = keywordService.getTopKeywords();

        // Verify results
        assert topKeywords.size() == 2;
        assert topKeywords.get(0).getName().equals(keyword2);
        assert topKeywords.get(0).getCount() == 2;
        assert topKeywords.get(1).getName().equals(keyword1);
        assert topKeywords.get(1).getCount() == 1;
    }

    private static class MockTypedTuple implements TypedTuple<String> {

        private final String name;
        private final Double score;

        MockTypedTuple(String name, Double score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String getValue() {
            return name;
        }

        @Override
        public Double getScore() {
            return score;
        }

        @Override
        public int compareTo(TypedTuple<String> o) {
            return this.score.compareTo(o.getScore());
        }
    }

}