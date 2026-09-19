package com.sanchay.exception;

public record ErrorResponse(
        int status,
        String message
) {
}