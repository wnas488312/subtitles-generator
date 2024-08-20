package com.wnas488312.subtitles_generator.exception;

/**
 * Exception mapped into 400 http code. Used when data provided in request is empty or incorrect.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
