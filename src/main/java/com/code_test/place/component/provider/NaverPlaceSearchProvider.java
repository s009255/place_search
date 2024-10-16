package com.code_test.place.component.provider;


import com.code_test.place.component.aspect.annotation.RedisCache;
import com.code_test.place.component.client.NaverApiClient;
import com.code_test.place.dto.Place;
import com.code_test.place.dto.external.NaverSearchResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class NaverPlaceSearchProvider implements PlaceSearchProvider {

    private final NaverApiClient NaverApiClient;

    public NaverPlaceSearchProvider(NaverApiClient NaverApiClient) {
        this.NaverApiClient = NaverApiClient;
    }

    @Override
    @RedisCache(prefix = "keyword.naver", params = {"keyword"}, type = Place.class)
    public Mono<List<Place>> getPlaces(String keyword) {
        return NaverApiClient.searchPlaces(keyword)
            .flatMapMany(response -> Flux.fromIterable(response.getItems()))
            .map(this::convertToPlace).collectList();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private Place convertToPlace(NaverSearchResponse.Item item) {
        return Place.builder()
            .name(item.getTitle())
            .address(item.getAddress())
            .longitude(ObjectUtils.isNotEmpty(item.getMapx()) ?
                Double.valueOf(item.getMapx()) / 10000000.0 : Double.NaN)
            .latitude(ObjectUtils.isNotEmpty(item.getMapy()) ?
                Double.valueOf(item.getMapy()) / 10000000.0 : Double.NaN)
            .build();
    }
}
