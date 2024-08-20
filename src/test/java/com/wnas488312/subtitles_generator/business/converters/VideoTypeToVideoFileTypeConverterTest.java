package com.wnas488312.subtitles_generator.business.converters;

import com.wnas488312.subtitles_generator.api.model.VideoType;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoTypeToVideoFileTypeConverterTest {

    @Test
    void convertTest() {
        VideoTypeToVideoFileTypeConverter converter = new VideoTypeToVideoFileTypeConverter();
        assertThat(converter.convert(VideoType.ORIGINAL)).isEqualTo(VideoFileType.ORIGINAL);
        assertThat(converter.convert(VideoType.SUBTITLES)).isEqualTo(VideoFileType.SUBTITLES);
        assertThat(converter.convert(VideoType.COMBINED)).isEqualTo(VideoFileType.COMBINED);
    }
}