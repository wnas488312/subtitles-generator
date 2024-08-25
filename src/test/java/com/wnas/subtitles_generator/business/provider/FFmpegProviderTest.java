package com.wnas.subtitles_generator.business.provider;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FFmpegProviderTest {

    @Test
    void getGrabberTest() {
        assertThat(new FFmpegProvider().getGrabber("somwere.mp4"))
                .isNotNull()
                .isInstanceOf(FFmpegFrameGrabber.class);
    }

    @Test
    void getRecorderTest() {
        assertThat(new FFmpegProvider().getRecorder("somwere.mp4", 100, 100))
                .isNotNull()
                .isInstanceOf(FFmpegFrameRecorder.class);
    }
}