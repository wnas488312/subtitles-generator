package com.wnas.subtitles_generator.business.generator;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Context of video generation. Used to pass arguments required to video generation to generator.
 * @param width                         Width of a video that will be generated.
 * @param height                        Height of a video that will be generated.
 * @param imagesWithFrameCoordinates    List of text chunks images that will be appended to video with frame coordinates.
 */
public record VideoGeneratorProcessingContext(
        Long dbVideoFileId,
        String originalFileName,
        int width,
        int height,
        List<ImageWithFrameCoordinates> imagesWithFrameCoordinates
) {

    /**
     * Object containing text chunk images with frame coordinates.
     * @param image         Image with text chunk and alpha background.
     * @param startFrame    Frame when image will start to be visible.
     * @param endFrame      Frame when image will end to be visible.
     */
    public record ImageWithFrameCoordinates(
            BufferedImage image,
            int startFrame,
            int endFrame
    ) {}
}
