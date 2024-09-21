package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.api.DefaultPropertiesApi;
import com.wnas.subtitles_generator.api.model.response.DefaultPropertiesResponse;
import com.wnas.subtitles_generator.business.converters.PropertiesFontColorToRgbColorObjectConverter;
import com.wnas.subtitles_generator.business.service.FontsService;
import com.wnas.subtitles_generator.config.properties.SubtitlesGenerationDefaultPropertiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DefaultPropertiesEndpoint implements DefaultPropertiesApi {
    private final SubtitlesGenerationDefaultPropertiesConfig config;
    private final PropertiesFontColorToRgbColorObjectConverter converter;
    private final FontsService fontsService;

    public DefaultPropertiesEndpoint(
            SubtitlesGenerationDefaultPropertiesConfig config,
            PropertiesFontColorToRgbColorObjectConverter converter,
            FontsService fontsService
    ) {
        this.config = config;
        this.converter = converter;
        this.fontsService = fontsService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultPropertiesResponse getDefaultProperties() {
        final List<String> defaultFontsNames = fontsService.getDefaultFontsNames();
        if (!CollectionUtils.contains(defaultFontsNames.listIterator(), config.getDefaultFontName())) {
            final String errorMessage = String.format("Default font name %s is not found in system fonts.", config.getDefaultFontName());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return new DefaultPropertiesResponse(
                config.getFontSize(),
                converter.convert(config.getFontColor()),
                config.getDefaultFontName(),
                defaultFontsNames,
                config.getBottomMargin()
        );
    }
}
