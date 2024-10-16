package com.code_test.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;


    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "Success", data);
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(status.value(), message, null);
    }


}
