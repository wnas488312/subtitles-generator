package com.wnas.subtitles_generator.business.converters;

import com.wnas.subtitles_generator.api.model.response.DefaultPropertiesColorResponse;
import com.wnas.subtitles_generator.config.properties.SubtitlesGenerationDefaultPropertiesFontColorConfig;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts Configuration color object into API color.
 */
@Component
public class SubtitlesGenerationDefaultPropertiesFontColorConfigToDefaultPropertiesColorResponseConverter implements Converter<SubtitlesGenerationDefaultPropertiesFontColorConfig, DefaultPropertiesColorResponse> {
    @Override
    public DefaultPropertiesColorResponse convert(SubtitlesGenerationDefaultPropertiesFontColorConfig source) {
        return new DefaultPropertiesColorResponse(source.getR(), source.getG(), source.getB());
    }
}
