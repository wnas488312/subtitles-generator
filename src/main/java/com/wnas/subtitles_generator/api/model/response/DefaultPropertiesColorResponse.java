package com.wnas.subtitles_generator.api.model.response;

/**
 * Structure used to store color on RGB format.
 * @param r red (0 - 255).
 * @param g green (0 - 255).
 * @param b blue (0 - 255).
 */
public record DefaultPropertiesColorResponse(Byte r, Byte g, Byte b) {
}
