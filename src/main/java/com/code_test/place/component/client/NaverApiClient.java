package com.code_test.place.component.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.code_test.place.dto.external.NaverSearchResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class NaverApiClient extends AbstractApiClient<NaverSearchResponse> {

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.client-secret}")
    private String naverClientSecret;


    public NaverApiClient(WebClient.Builder webClientBuilder,
        ObjectMapper objectMapper) {
        super(webClientBuilder, objectMapper);
    }

    @Override
    protected String getBaseUrl() {
        return "https://openapi.naver.com/v1";
    }

    @Override
    protected String getSearchPath() {
        return "/search/local.json";
    }

    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Naver-Client-Id", naverClientId);
        headers.put("X-Naver-Client-Secret", naverClientSecret);
        return headers;
    }

    @Override
    protected Map<String, String> getQueryParams(String keyword) {
        return Map.of("query", keyword, "display", "5");

    }


    @Override
    protected NaverSearchResponse handleResponse(String responseBody) {
        NaverSearchResponse response;
        try {
            response = objectMapper.readValue(responseBody,
                NaverSearchResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

