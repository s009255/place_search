package com.test.place.controller;

import com.test.place.dto.ApiResponse;
import com.test.place.dto.Keyword;
import com.test.place.dto.Place;
import com.test.place.service.KeywordService;
import com.test.place.service.PlaceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final KeywordService keywordService;


    @GetMapping("/places/search")
    public Mono<ApiResponse<List<Place>>> searchPlaces(
        @RequestParam(value = "keyword") String keyword) {
        if (ObjectUtils.isEmpty(keyword)) {
            return Mono.just(ApiResponse.error("Keyword is empty.", HttpStatus.BAD_REQUEST));
        }
        return placeService.searchPlaces(keyword)
            .flatMap(places -> {
                if (places.isEmpty()) {
                    return Mono.just(ApiResponse.error(
                        "No places found.", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(ApiResponse.success(places));
                }
            });
    }

    @GetMapping("/places/keywords")
    public ApiResponse<List<Keyword>> getPopularKeywords() {
        return ApiResponse.success(keywordService.getTopKeywords());
    }


}
