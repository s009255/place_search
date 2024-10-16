package com.test.place.component.provider;


import com.test.place.component.aspect.annotation.RedisCache;
import com.test.place.component.client.KakaoApiClient;
import com.test.place.dto.Place;
import com.test.place.dto.external.KakaoSearchResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KakaoPlaceSearchProvider implements PlaceSearchProvider {

    private final KakaoApiClient kakaoApiClient;

    public KakaoPlaceSearchProvider(KakaoApiClient kakaoApiClient) {
        this.kakaoApiClient = kakaoApiClient;
    }

    @Override
    @RedisCache(prefix = "keyword.kakao", params = {"keyword"}, type = Place.class)
    public Mono<List<Place>> getPlaces(String keyword) {
        return kakaoApiClient.searchPlaces(keyword)
            .flatMapMany(response -> Flux.fromIterable(response.getDocuments()))
            .map(this::convertToPlace).collectList();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    private Place convertToPlace(KakaoSearchResponse.Document document) {
        return Place.builder()
            .name(document.getPlaceName())
            .address(document.getAddressName())
            .longitude(ObjectUtils.isNotEmpty(document.getX()) ?
                Double.valueOf(document.getX()) : Double.NaN)
            .latitude(ObjectUtils.isNotEmpty(document.getY()) ?
                Double.valueOf(document.getY()) : Double.NaN)
            .build();
    }

}
