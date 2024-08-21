package com.wnas.subtitles_generator.business.service.message;

/**
 * Response object for progress web socket.
 * @param stage     Stage of a subtitles processing.
 * @param progress  Progress in percentage of entire process,
 */
public record ProgressMessage(String stage, Integer progress) {
}
