package com.wnas.subtitles_generator.business.converters;

import com.wnas.subtitles_generator.api.model.RgbColorObject;
import com.wnas.subtitles_generator.config.properties.PropertiesFontColor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesFontColorToRgbColorObjectConverterTest {

    @Test
    void convertTest() {
        final PropertiesFontColor source = new PropertiesFontColor(100, 150, 200);
        final RgbColorObject converted = new PropertiesFontColorToRgbColorObjectConverter()
                .convert(source);

        assertThat(converted).isNotNull();
        assertThat(converted.r()).isEqualTo(source.getR());
        assertThat(converted.g()).isEqualTo(source.getG());
        assertThat(converted.b()).isEqualTo(source.getB());
    }
}