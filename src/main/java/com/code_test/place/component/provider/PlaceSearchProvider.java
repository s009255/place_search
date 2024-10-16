package com.test.place.component.provider;


import com.test.place.dto.Place;
import java.util.List;
import reactor.core.publisher.Mono;

public interface PlaceSearchProvider {

    Mono<List<Place>> getPlaces(String keyword);

    int getPriority();



}
