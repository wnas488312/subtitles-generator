package com.wnas488312.subtitles_generator.global;

import com.wnas488312.subtitles_generator.api.model.response.BasicErrorResponse;
import com.wnas488312.subtitles_generator.exception.BadRequestException;
import com.wnas488312.subtitles_generator.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler used to map HTTP status code related exceptions into error response with proper status code
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BasicErrorResponse> handleResourceNotFoundException(NotFoundException ex) {
        log.warn("Responding with HTTP 404 code: ", ex);
        BasicErrorResponse errorResponse = new BasicErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BasicErrorResponse> handleBadRequestException(BadRequestException ex) {
        log.warn("Responding with HTTP 400 code: ", ex);
        BasicErrorResponse errorResponse = new BasicErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
