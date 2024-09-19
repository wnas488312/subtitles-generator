package com.wnas.subtitles_generator.business.converters;

import com.wnas.subtitles_generator.api.model.response.DefaultPropertiesColorResponse;
import com.wnas.subtitles_generator.config.properties.SubtitlesGenerationDefaultPropertiesFontColorConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubtitlesGenerationDefaultPropertiesFontColorConfigToDefaultPropertiesColorResponseConverterTest {

    @Test
    void convertTest() {
        final SubtitlesGenerationDefaultPropertiesFontColorConfig source = new SubtitlesGenerationDefaultPropertiesFontColorConfig(100, 150, 200);
        final DefaultPropertiesColorResponse converted = new SubtitlesGenerationDefaultPropertiesFontColorConfigToDefaultPropertiesColorResponseConverter()
                .convert(source);

        assertThat(converted).isNotNull();
        assertThat(converted.r()).isEqualTo(source.getR());
        assertThat(converted.g()).isEqualTo(source.getG());
        assertThat(converted.b()).isEqualTo(source.getB());
    }
}