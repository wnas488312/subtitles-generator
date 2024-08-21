package com.wnas.subtitles_generator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s-generator.config")
@Getter
@Setter
public class AppConfig {
    private String tempFilePrefix;
    private String videoFormat;
    private String videoCodecName;
    private Integer frameRate;
}
