package com.wnas488312.subtitles_generator.business.service.message;

import lombok.Getter;

/**
 * Enum with stages of subtitles processing with human friendly messages pinned to them.
 */
@Getter
public enum GenerationProgressStage {
    IMAGES("Generating images..."),
    SUBTITLES("Combining images..."),
    VIDEO("Generating video..."),
    DONE("Done."),
    ERROR("Error.");

    private final String stringValue;

    GenerationProgressStage(String stringValue) {
        this.stringValue = stringValue;
    }
}
