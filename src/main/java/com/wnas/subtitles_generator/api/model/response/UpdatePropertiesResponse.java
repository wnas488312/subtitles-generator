package com.wnas.subtitles_generator.api.model.response;

import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response of an endpoint for updating subtitles generation process properties.
 * @param id            Identifier of a subtitles generation process,
 * @param width         Width of a video in pixels.
 * @param height        Height of a video in pixels.
 * @param bottomMargin  Distance between text and bottom frame in pixels.
 * @param fontName      Name of a font that will be used in appended subtitles in video.
 * @param textChunks    Chunks of subtitles text with frame coordinates.
 * @param creationDate  Date and time when subtitles generation process started.
 */
public record UpdatePropertiesResponse(
        Long id,
        Integer width,
        Integer height,
        Integer bottomMargin,
        String fontName,
        List<SubtitlesTextChunk> textChunks,
        LocalDateTime creationDate
) {
}
