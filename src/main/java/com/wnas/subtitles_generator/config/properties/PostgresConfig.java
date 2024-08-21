package com.wnas.subtitles_generator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Getter
@Setter
public class PostgresConfig {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
