package com.wnas488312.subtitles_generator.business.converters;

import com.wnas488312.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas488312.subtitles_generator.data.entity.TextChunk;
import com.wnas488312.subtitles_generator.testData.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextChunksToSubtitlesTextChunkConverterTest {

    @Test
    void convertTest() {
        final TextChunk source = TestData.textChunk();
        final SubtitlesTextChunk converted = new TextChunksToSubtitlesTextChunkConverter().convert(source);

        assertThat(converted).isNotNull();
        assertThat(converted.text()).isEqualTo(source.getText());
        assertThat(converted.startFrame()).isEqualTo(source.getStartFrame());
        assertThat(converted.endFrame()).isEqualTo(source.getEndFrame());
    }
}