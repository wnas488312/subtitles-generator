package com.wnas.subtitles_generator.api.model.response;

import java.util.List;

/**
 * Response of processing text input endpoint.
 * Contains text input split into chunks of text.
 * @param message
 */
public record InputProcessResponse(List<String> message) {
}
