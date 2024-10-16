package com.code_test.place.dto.external;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class KakaoSearchResponse {

    private Meta meta;
    private List<Document> documents;

    // Getters and Setters 생략

    @Getter
    @NoArgsConstructor
    public static class Meta {

        @JsonProperty("same_name")
        private SameName sameName;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("total_count")
        private int totalCount;

        @JsonProperty("is_end")
        private boolean isEnd;

        // Getters and Setters 생략

        public static class SameName {

            @JsonProperty("region")
            private List<String> region;

            @JsonProperty("keyword")
            private String keyword;

            @JsonProperty("selected_region")
            private String selectedRegion;

            // Getters and Setters 생략
        }
    }

    @Getter
    @NoArgsConstructor
    @Setter
    public static class Document {

        @JsonProperty("place_name")
        private String placeName;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("place_url")
        private String placeUrl;

        @JsonProperty("category_name")
        private String categoryName;

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("road_address_name")
        private String roadAddressName;

        @JsonProperty("id")
        private String id;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("category_group_code")
        private String categoryGroupCode;

        @JsonProperty("category_group_name")
        private String categoryGroupName;

        @JsonProperty("x")
        private String x;

        @JsonProperty("y")
        private String y;

    }
}
