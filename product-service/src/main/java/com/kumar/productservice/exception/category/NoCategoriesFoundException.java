package com.kumar.productservice.exception.category;

public class NoCategoriesFoundException extends RuntimeException {
    public NoCategoriesFoundException(String message) {
        super(message);
    }
}
