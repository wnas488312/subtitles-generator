package com.wnas488312.subtitles_generator.exception;

/**
 * Exception mapped into 404 http code. Used when resource is not found.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
