package com.test.place.service;


import com.test.place.component.provider.PlaceSearchProvider;
import com.test.place.dto.Place;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.retry.Retry;


@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final KeywordService keywordService;
    private final List<PlaceSearchProvider> placeSearchProviderList;

    private Mono<Long> findTotalIndex(Place targetPlace, List<List<Place>> searchResultList) {
        return Flux.fromIterable(searchResultList).index().filter(
                tuple -> tuple.getT2().stream().anyMatch(place -> isSamePlace(place, targetPlace)))
            .map(Tuple2::getT1).next().defaultIfEmpty(Long.MAX_VALUE);  // 찾지 못했을 경우
    }


    public Mono<List<Place>> searchPlaces(String keyword) {
        Mono.fromRunnable(() -> keywordService.addKeyword(keyword))
            .subscribeOn(Schedulers.boundedElastic())
            .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))).doOnError(e -> {
                log.error("Failed to add keyword '{}'.", keyword, e);
            }).subscribe();

        List<Mono<List<Place>>> searchResultList = placeSearchProviderList.stream()
            .sorted(Comparator.comparingInt(PlaceSearchProvider::getPriority))
            .map(placeSearchProvider -> placeSearchProvider.getPlaces(keyword))
            .collect(Collectors.toList());

        return Flux.concat(searchResultList).collectList()
            .flatMap(results -> {
                Map<Place, Long> placeCountMap = new LinkedHashMap<>();
                results.forEach(placeList -> {
                    for (Place place : placeList) {
                        Place existingPlace = placeCountMap.keySet().stream()
                            .filter(p -> isSamePlace(p, place)).findFirst().orElse(null);

                        if (existingPlace == null) {
                            placeCountMap.put(place, 1L);
                        } else {
                            placeCountMap.put(existingPlace, placeCountMap.get(existingPlace) + 1);
                        }
                    }
                });

                return Flux.fromIterable(placeCountMap.entrySet()).flatMap(
                        entry -> findTotalIndex(entry.getKey(), results).map(
                            index -> Map.entry(entry.getKey(),
                                new AbstractMap.SimpleEntry<>(entry.getValue(), index))))
                    .sort((e1, e2) -> {
                        int compared = e2.getValue().getKey().compareTo(e1.getValue().getKey());
                        if (compared != 0) {
                            return compared;
                        } else {
                            return Long.compare(e1.getValue().getValue(), e2.getValue().getValue());
                        }
                    }).map(Map.Entry::getKey).collectList();
            });


    }


    private boolean isSamePlace(Place fstPlace, Place secPlace) {
        return (
            isSameLocation(fstPlace.getLongitude(), fstPlace.getLatitude(), secPlace.getLongitude(),
                secPlace.getLatitude()) && isSameName(fstPlace.getName(), secPlace.getName()));
    }

    private boolean isSameLocation(double x1, double y1, double x2, double y2) {
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double threshold = 0.0001;  // 허용 거리 차이 (위도, 경도로 약 10m 정도)
        return distance < threshold;
    }


    private boolean isSameName(String name1, String name2) {
        if (ObjectUtils.isEmpty(name1) || ObjectUtils.isEmpty(name2)) {
            return false;
        }

        LevenshteinDistance distance = new LevenshteinDistance();
        int threshold = 2;
        return distance.apply(name1.replaceAll(" ", ""), name2.replaceAll(" ", "")) <= threshold;
    }


}
