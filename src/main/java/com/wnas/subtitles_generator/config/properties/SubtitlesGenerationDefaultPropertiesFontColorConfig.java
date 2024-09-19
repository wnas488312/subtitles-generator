package com.wnas.subtitles_generator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s-generator.subtitles-generation.font-color")
@Getter
@Setter
public class SubtitlesGenerationDefaultPropertiesFontColorConfig {
    private Byte r;
    private Byte g;
    private Byte b;
}
