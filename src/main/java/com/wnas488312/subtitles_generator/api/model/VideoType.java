package com.wnas488312.subtitles_generator.api.model;

/**
 * Type of video file stored on the server.
 */
public enum VideoType {

    /**
     * Original video file.
     */
    ORIGINAL,

    /**
     * Video with subtitles only.
     */
    SUBTITLES,

    /**
     * Original video with appended subtitles.
     */
    COMBINED
}
