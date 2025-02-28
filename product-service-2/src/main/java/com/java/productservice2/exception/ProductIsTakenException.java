package com.java.productservice2.exception;

public class ProductIsTakenException extends RuntimeException {
    public ProductIsTakenException(String message) {
        super(message);
    }
}
