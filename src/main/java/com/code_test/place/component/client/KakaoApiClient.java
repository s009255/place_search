package com.code_test.place.component.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.code_test.place.dto.external.KakaoSearchResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class KakaoApiClient extends AbstractApiClient<KakaoSearchResponse> {

    @Value("${kakao.api-key}")
    private String kakaoApiKey;


    public KakaoApiClient(WebClient.Builder webClientBuilder,
        ObjectMapper objectMapper) {
        super(webClientBuilder, objectMapper);
    }

    @Override
    protected String getBaseUrl() {
        return "https://dapi.kakao.com/v2";
    }

    @Override
    protected String getSearchPath() {
        return "/local/search/keyword.json";
    }

    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "KakaoAK " + kakaoApiKey);
        return headers;
    }

    @Override
    protected Map<String, String> getQueryParams(String keyword) {
        return Map.of("query", keyword, "size", "5");
    }


    @Override
    protected KakaoSearchResponse handleResponse(String responseBody) {
        KakaoSearchResponse response;
        try {
            response = objectMapper.readValue(responseBody,
                KakaoSearchResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

