package com.kumar.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private T data;

    private String errorCode;

    private String path;

    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(
            T data,
            String message,
            String path) {

        return new ApiResponse<>(
                true,
                message,
                data,
                null,
                path,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> error(
            String message,
            String errorCode,
            String path) {

        return new ApiResponse<>(
                false,
                message,
                null,
                errorCode,
                path,
                LocalDateTime.now()
        );
    }
}