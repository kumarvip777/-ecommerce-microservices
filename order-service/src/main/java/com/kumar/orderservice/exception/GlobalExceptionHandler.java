package com.kumar.orderservice.exception;

import com.kumar.orderservice.exception.order.ProductServiceUnavailableException;
import com.kumar.orderservice.payload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductServiceUnavailable(
            ProductServiceUnavailableException ex,
            HttpServletRequest request) {

        ApiResponse<Void> response =
                ApiResponse.error(
                        ex.getMessage(),
                        "PRODUCT_SERVICE_UNAVAILABLE",
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}