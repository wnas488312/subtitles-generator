package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.api.model.response.DefaultPropertiesColorResponse;
import com.wnas.subtitles_generator.api.model.response.DefaultPropertiesResponse;
import com.wnas.subtitles_generator.business.converters.SubtitlesGenerationDefaultPropertiesFontColorConfigToDefaultPropertiesColorResponseConverter;
import com.wnas.subtitles_generator.business.service.FontsService;
import com.wnas.subtitles_generator.config.properties.SubtitlesGenerationDefaultPropertiesConfig;
import com.wnas.subtitles_generator.config.properties.SubtitlesGenerationDefaultPropertiesFontColorConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPropertiesEndpointTest {
    private static final String DEFAULT_FONT_NAME = "Arial";
    private static final Integer BOTTOM_MARGIN = 50;
    private static final Integer FONT_SIZE = 12;
    private static final DefaultPropertiesColorResponse DEFAULT_FONT_COLOR = new DefaultPropertiesColorResponse(200, 200, 200);
    private static final List<String> FONTS_NAMES = List.of(DEFAULT_FONT_NAME, "Sans Sarif");

    @Mock
    private SubtitlesGenerationDefaultPropertiesConfig config;
    @Mock
    private SubtitlesGenerationDefaultPropertiesFontColorConfigToDefaultPropertiesColorResponseConverter converter;
    @Mock
    private FontsService fontsService;

    @InjectMocks
    private DefaultPropertiesEndpoint endpoint;

    @Test
    void getDefaultPropertiesTest() {
        when(config.getDefaultFontName()).thenReturn(DEFAULT_FONT_NAME);
        when(config.getBottomMargin()).thenReturn(BOTTOM_MARGIN);
        when(config.getFontSize()).thenReturn(FONT_SIZE);
        when(config.getFontColor()).thenReturn(new SubtitlesGenerationDefaultPropertiesFontColorConfig(200, 200, 200));

        when(fontsService.getDefaultFontsNames()).thenReturn(FONTS_NAMES);

        when(converter.convert(any(SubtitlesGenerationDefaultPropertiesFontColorConfig.class))).thenReturn(DEFAULT_FONT_COLOR);

        DefaultPropertiesResponse response = endpoint.getDefaultProperties();

        assertThat(response).isNotNull();
        assertThat(response.defaultFontName()).isEqualTo(DEFAULT_FONT_NAME);
        assertThat(response.bottomMargin()).isEqualTo(BOTTOM_MARGIN);
        assertThat(response.fontSize()).isEqualTo(FONT_SIZE);
        assertThat(response.fontColor()).isEqualTo(DEFAULT_FONT_COLOR);
        assertThat(response.fontNames()).isEqualTo(FONTS_NAMES);
    }

    @Test
    void getDefaultProperties_defaultFontNameUnknown_expectErrorTest() {
        when(config.getDefaultFontName()).thenReturn(DEFAULT_FONT_NAME);
        when(fontsService.getDefaultFontsNames()).thenReturn(List.of("Something unknown", "Sans Sarif"));

        assertThatThrownBy(() -> endpoint.getDefaultProperties())
                .hasMessageContaining(String.format("Default font name %s is not found in system fonts.", DEFAULT_FONT_NAME));
    }
}