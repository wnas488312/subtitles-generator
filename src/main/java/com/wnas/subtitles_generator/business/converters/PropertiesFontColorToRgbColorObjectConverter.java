package com.wnas.subtitles_generator.business.converters;

import com.wnas.subtitles_generator.api.model.RgbColorObject;
import com.wnas.subtitles_generator.config.properties.PropertiesFontColor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts Configuration color object into API color.
 */
@Component
public class PropertiesFontColorToRgbColorObjectConverter implements Converter<PropertiesFontColor, RgbColorObject> {
    @Override
    public RgbColorObject convert(PropertiesFontColor source) {
        return new RgbColorObject(source.getR(), source.getG(), source.getB());
    }
}
