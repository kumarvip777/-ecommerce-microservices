package com.kumar.orderservice.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private T data;

}