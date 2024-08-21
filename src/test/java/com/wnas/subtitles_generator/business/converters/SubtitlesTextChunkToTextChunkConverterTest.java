package com.wnas.subtitles_generator.business.converters;

import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas.subtitles_generator.data.entity.TextChunk;
import com.wnas.subtitles_generator.testData.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubtitlesTextChunkToTextChunkConverterTest {

    @Test
    void convertTest() {
        final SubtitlesTextChunk source = TestData.subtitlesTextChunk();
        final TextChunk converted = new SubtitlesTextChunkToTextChunkConverter().convert(source);

        assertThat(converted).isNotNull();
        assertThat(converted.getText()).isEqualTo(source.text());
        assertThat(converted.getStartFrame()).isEqualTo(source.startFrame());
        assertThat(converted.getEndFrame()).isEqualTo(source.endFrame());
    }
}