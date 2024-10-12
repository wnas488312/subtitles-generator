package com.wnas.subtitles_generator.api.model.response;

import com.wnas.subtitles_generator.api.model.RgbColorObject;

import java.util.List;

/**
 * Structure used to store default values of subtitles display.
 * @param fontSize          Size of a font in pixels.
 * @param fontColor         Color of a font.
 * @param defaultFontName   Name of a font that will be used as default one.
 * @param fontNames         List of names of available fonts.
 * @param bottomMargin      Distance in pixels from subtitles to video bottom.
 * @param outlineInPixels   Size of a subtitles text outline in pixels.
 */
public record DefaultPropertiesResponse(
        Integer fontSize,
        RgbColorObject fontColor,
        String defaultFontName,
        List<String> fontNames,
        Integer bottomMargin,
        Integer outlineInPixels
) {
}
