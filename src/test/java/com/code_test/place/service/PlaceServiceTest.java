package com.code_test.place.service;

import static org.mockito.Mockito.when;

import com.code_test.place.component.provider.PlaceSearchProvider;
import com.code_test.place.dto.Place;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @InjectMocks
    private PlaceService placeService;

    @Mock
    private KeywordService keywordService;

    @Mock
    private PlaceSearchProvider placeSearchProvider1;

    @Mock
    private PlaceSearchProvider placeSearchProvider2;

    @Mock
    private PlaceSearchProvider placeSearchProvider3;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        placeService = new PlaceService(keywordService,
            Arrays.asList(placeSearchProvider1, placeSearchProvider2, placeSearchProvider3));

    }

    @Test
    @DisplayName("Test searchPlaces with 3 API ")
    void searchPlaces() {
        String keyword = "testKeyword";
        Place place1 = new Place("Place1", "address1", 1.0, 1.0);
        Place place2 = new Place("Place2", "address2", 2.0, 2.0);
        Place place3 = new Place("Place3", "address3", 3.0, 3.0);
        Place place4 = new Place("Place1_1", "address1", 1.0, 1.0);
        Place place5 = new Place("Place2_1", "address2", 2.0, 2.0);
        Place place6 = new Place("Place6", "address6", 6.0, 6.0);

        when(placeSearchProvider1.getPlaces(keyword)).thenReturn(
            Mono.just(Arrays.asList(place1, place2)));
        when(placeSearchProvider2.getPlaces(keyword)).thenReturn(
            Mono.just(Arrays.asList(place3, place4)));
        when(placeSearchProvider3.getPlaces(keyword)).thenReturn(
            Mono.just(Arrays.asList(place5, place6)));

        Mono<List<Place>> result = placeService.searchPlaces(keyword);

        StepVerifier.create(result)
            .expectNextMatches(places -> places.size() == 4 &&
                places.contains(place1) &&
                places.contains(place2) &&
                places.contains(place3) &&
                places.contains(place6) &&
                !places.contains(place4) &&
                !places.contains(place5))
            .verifyComplete();

    }
}