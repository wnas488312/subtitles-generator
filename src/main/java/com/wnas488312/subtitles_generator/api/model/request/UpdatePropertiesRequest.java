package com.wnas488312.subtitles_generator.api.model.request;

import java.util.List;

/**
 * Request for endpoint for updating subtitles process properties.
 * @param bottomMargin  Distance in pixels from bottom of a video to text display.
 * @param fontName      Name of a font that will be used in text.
 * @param textChunks    List of chunks of text that will append in video with frame coordinates.
 */
public record UpdatePropertiesRequest(
        Integer bottomMargin,
        String fontName,
        List<SubtitlesTextChunk> textChunks
) {
}
