package com.test.place.dto.external;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverSearchResponse {

    @JsonProperty("lastBuildDate")
    private String lastBuildDate;

    @JsonProperty("total")
    private int total;

    @JsonProperty("start")
    private int start;

    @JsonProperty("display")
    private int display;

    @JsonProperty("items")
    private List<Item> items;

    @Getter
    @NoArgsConstructor
    public static class Item {

        @JsonProperty("title")
        private String title;

        @JsonProperty("link")
        private String link;

        @JsonProperty("category")
        private String category;

        @JsonProperty("description")
        private String description;

        @JsonProperty("telephone")
        private String telephone;

        @JsonProperty("address")
        private String address;

        @JsonProperty("roadAddress")
        private String roadAddress;

        @JsonProperty("mapx")
        private String mapx;

        @JsonProperty("mapy")
        private String mapy;

    }

}

