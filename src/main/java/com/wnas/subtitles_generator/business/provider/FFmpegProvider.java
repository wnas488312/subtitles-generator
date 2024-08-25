package com.wnas.subtitles_generator.business.provider;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.springframework.stereotype.Component;

/**
 * Provider class that can be mocked in tests, that creates {@link FFmpegFrameGrabber} and {@link FFmpegFrameRecorder}.
 */
@Component
public class FFmpegProvider {

    /**
     * Creates new {@link FFmpegFrameGrabber}.
     * @param filePath  Path to video file.
     * @return          New frame grabber.
     */
    public FFmpegFrameGrabber getGrabber(String filePath) {
        return new FFmpegFrameGrabber(filePath);
    }

    /**
     * Creates new {@link FFmpegFrameRecorder}.
     * @param filePath  Path to video file.
     * @param width     Width of a video.
     * @param height    Height of a video.
     * @return          New frame recorder.
     */
    public FFmpegFrameRecorder getRecorder(String filePath, int width, int height) {
        return new FFmpegFrameRecorder(filePath, width, height);
    }
}
