package com.wnas.subtitles_generator.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s-generator.subtitles-generation.font-color")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertiesFontColor {
    private Integer r;
    private Integer g;
    private Integer b;
}
