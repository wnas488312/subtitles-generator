package com.wnas.subtitles_generator.api.model.request;

import com.wnas.subtitles_generator.api.model.RgbColorObject;

import java.util.List;

/**
 * Request for endpoint for updating subtitles process properties.
 * @param bottomMargin      Distance in pixels from bottom of a video to text display.
 * @param fontName          Name of a font that will be used in text.
 * @param fontColor         Color of a font.
 * @param fontSize          Size of a font in pixels.
 * @param textChunks        List of chunks of text that will append in video with frame coordinates.
 * @param outlineInPixels   Size of a subtitles text outline in pixels.
 */
public record UpdatePropertiesRequest(
        Integer bottomMargin,
        String fontName,
        RgbColorObject fontColor,
        Integer fontSize,
        List<SubtitlesTextChunk> textChunks,
        Integer outlineInPixels
) {
}
