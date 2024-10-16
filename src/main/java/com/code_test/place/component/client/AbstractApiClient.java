package com.test.place.component.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractApiClient<T> {

    protected final WebClient webClient;
    protected final ObjectMapper objectMapper;


    @Autowired
    public AbstractApiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
            .baseUrl(getBaseUrl())
            .build();
        this.objectMapper = objectMapper;
    }


    protected abstract String getBaseUrl();

    protected abstract String getSearchPath();

    protected abstract T handleResponse(String responseBody);

    protected abstract Map<String, String> getHeaders();

    protected abstract Map<String, String> getQueryParams(String keyword);


    public Mono<T> searchPlaces(String keyword) {
        log.info("### API call start ###");

        WebClient.RequestHeadersSpec<?> request = webClient.get()
            .uri(uriBuilder -> {
                uriBuilder.path(getSearchPath());
                getQueryParams(keyword).forEach(uriBuilder::queryParam);
                return uriBuilder.build();
            });

        for (Map.Entry<String, String> header : getHeaders().entrySet()) {
            request = request.header(header.getKey(), header.getValue());
        }

        return request.retrieve()
            .onStatus(status -> status.isError(),
                response -> {
                    log.error("API Status failed: {}", response.statusCode());
                    return Mono.empty();
                })
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(10))
            .map(this::handleResponse)
            .onErrorResume(WebClientResponseException.class, e -> {
                log.error("API call failed: ", e);
                return Mono.empty();
            })
            .onErrorResume(Exception.class, e -> {
                log.error("An error occurred: ", e);
                return Mono.empty();
            });
    }

}

