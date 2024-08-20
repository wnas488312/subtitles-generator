package com.wnas488312.subtitles_generator.api.model.request;

/**
 * Chunk of text with it frame coordinates that will be placed in the video.
 * @param text          Chunk of text.
 * @param startFrame    Frame where text display will start.
 * @param endFrame      Frame where text display will end.
 */
public record SubtitlesTextChunk(
        String text,
        int startFrame,
        int endFrame
) { }
