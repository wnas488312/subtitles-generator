package com.wnas.subtitles_generator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s-generator.subtitles-generation")
@Getter
@Setter
public class SubtitlesGenerationDefaultPropertiesConfig {
    private Integer fontSize;
    private PropertiesFontColor fontColor;
    private String defaultFontName;
    private Integer bottomMargin;
}
