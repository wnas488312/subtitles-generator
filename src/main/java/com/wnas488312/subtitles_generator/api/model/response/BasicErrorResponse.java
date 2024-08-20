package com.wnas488312.subtitles_generator.api.model.response;

/**
 * Response body used when 4xx error occurs.
 * @param errorMessage  Details about given error
 * @param timestamp     Timestamp of a moment when error appeared
 */
public record BasicErrorResponse(String errorMessage, long timestamp) { }
