package com.code_test.place.component.provider;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.code_test.place.component.client.KakaoApiClient;
import com.code_test.place.component.client.NaverApiClient;
import com.code_test.place.dto.Place;
import com.code_test.place.dto.external.KakaoSearchResponse;
import com.code_test.place.dto.external.NaverSearchResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/*
 * Project : location-search-server
 * Class : com.code_test.place.component.provider.PlaceSearchProviderTest
 * Version : 24/10/16 v0.0.1
 * Created by user on 24/10/16.
 * *** 저작권 주의 ***
 */

class PlaceSearchProviderTest {

    @Mock
    private KakaoApiClient kakaoApiClient;

    @InjectMocks
    private KakaoPlaceSearchProvider kakaoPlaceSearchProvider;

    @Mock
    private NaverApiClient naverApiClient;

    @InjectMocks
    private NaverPlaceSearchProvider naverPlaceSearchProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getPlaces method for Kakao")
    void getKakaoPlaces() {
        String keyword = "testKeyword";
        KakaoSearchResponse response = new KakaoSearchResponse();
        KakaoSearchResponse.Document document = new KakaoSearchResponse.Document();
        document.setPlaceName("Test Place");
        document.setAddressName("Test Address");
        document.setX("127.0");
        document.setY("37.0");
        response.setDocuments(Collections.singletonList(document));

        when(kakaoApiClient.searchPlaces(anyString())).thenReturn(Mono.just(response));

        Mono<List<Place>> result = kakaoPlaceSearchProvider.getPlaces(keyword);

        StepVerifier.create(result)
            .expectNextMatches(
                places -> places.size() == 1 && places.get(0).getName().equals("Test Place")
                    && places.get(0).getAddress().equals("Test Address")
                    && places.get(0).getLongitude() == 127.0
                    && places.get(0).getLatitude() == 37.0)

            .verifyComplete();
    }

    @Test
    @DisplayName("Test getPlaces method for Naver")
    void getNaverPlaces() {
        String keyword = "testKeyword";
        NaverSearchResponse response = new NaverSearchResponse();
        NaverSearchResponse.Item item = new NaverSearchResponse.Item();
        item.setTitle("Test Place");
        item.setAddress("Test Address");
        item.setMapx("1270000000");
        item.setMapy("370000000");
        response.setItems(Collections.singletonList(item));

        when(naverApiClient.searchPlaces(anyString())).thenReturn(Mono.just(response));

        Mono<List<Place>> result = naverPlaceSearchProvider.getPlaces(keyword);

        StepVerifier.create(result)
            .expectNextMatches(
                places -> places.size() == 1 && places.get(0).getName().equals("Test Place")
                    && places.get(0).getAddress().equals("Test Address")
                    && places.get(0).getLongitude() == 127.0
                    && places.get(0).getLatitude() == 37.0)
            .verifyComplete();
    }

}