package com.test.place.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Place {

    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private String address;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("latitude")
    private double latitude;


    @Builder
    public Place(String name, String address, double longitude, double latitude) {
        this.name = name.replaceAll("<[^>]*>", "");
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;

    }

}
