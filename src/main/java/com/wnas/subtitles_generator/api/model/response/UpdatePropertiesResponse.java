package com.wnas.subtitles_generator.api.model.response;

import com.wnas.subtitles_generator.api.model.RgbColorObject;
import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response of an endpoint for updating subtitles generation process properties.
 * @param id                Identifier of a subtitles generation process,
 * @param width             Width of a video in pixels.
 * @param height            Height of a video in pixels.
 * @param bottomMargin      Distance between text and bottom frame in pixels.
 * @param fontName          Name of a font that will be used in appended subtitles in video.
 * @param fontColor         Color og a font in RGB.
 * @param fontSize          Size of a font in pixels.
 * @param textChunks        Chunks of subtitles text with frame coordinates.
 * @param creationDate      Date and time when subtitles generation process started.
 * @param outlineInPixels   Size of a subtitles text outline in pixels.
 */
public record UpdatePropertiesResponse(
        Long id,
        Integer width,
        Integer height,
        Integer bottomMargin,
        String fontName,
        RgbColorObject fontColor,
        Integer fontSize,
        List<SubtitlesTextChunk> textChunks,
        LocalDateTime creationDate,
        Integer outlineInPixels
) {
}
