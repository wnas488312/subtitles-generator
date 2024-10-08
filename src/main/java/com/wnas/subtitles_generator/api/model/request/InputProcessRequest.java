package com.wnas.subtitles_generator.api.model.request;

/**
 * Request for processing text input endpoint.
 * @param input text input to process.
 */
public record InputProcessRequest(String input) {
}
