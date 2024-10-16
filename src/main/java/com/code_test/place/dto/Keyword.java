package com.test.place.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Keyword {

    @JsonProperty("name")
    private String name;
    @JsonProperty("count")
    private Integer count;

    @Builder
    public Keyword(String name, Integer count) {
        this.name = name;
        this.count = count;
    }
}
