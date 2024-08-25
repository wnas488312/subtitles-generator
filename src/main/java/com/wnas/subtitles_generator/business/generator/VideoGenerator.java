package com.wnas.subtitles_generator.business.generator;

/**
 * Component used to generate videos.
 */
public interface VideoGenerator {

    /**
     * Turns given images list into video, saves it on disk, and creates video DB entry.
     * @param data object containing images array with video data
     * @throws Exception when recorder error occurs
     */
    void generateVideoFromImages(VideoGeneratorProcessingContext data) throws Exception;

    /**
     * Appends list of subtitles text into original video, saves it on disk, and creates video DB entry.
     * @param data          Object containing text to append with frame coordinates.
     * @throws Exception    When file cannot be created or bytedeco classes throws an error.
     */
    void combineOriginalVideoWithSubtitles(VideoGeneratorProcessingContext data) throws Exception;
}
