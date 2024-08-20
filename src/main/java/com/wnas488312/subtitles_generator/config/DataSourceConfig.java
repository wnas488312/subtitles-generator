package com.wnas488312.subtitles_generator.config;

import com.wnas488312.subtitles_generator.config.properties.H2Config;
import com.wnas488312.subtitles_generator.config.properties.PostgresConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
public class DataSourceConfig {
    private final PostgresConfig postgresConfig;
    private final H2Config h2Config;

    public DataSourceConfig(PostgresConfig postgresConfig, H2Config h2Config) {
        this.postgresConfig = postgresConfig;
        this.h2Config = h2Config;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                try {
                    Class.forName(postgresConfig.getDriverClassName());
                    return "postgres";
                } catch (ClassNotFoundException e) {
                    log.warn("Running Postgres instance not found. Embedded database will be used");
                    return "h2";
                }
            }
        };

        DataSource postgresDataSource = createDataSource(postgresConfig.getUrl(), postgresConfig.getUsername(), postgresConfig.getPassword(), postgresConfig.getDriverClassName());
        DataSource h2DataSource = createDataSource(h2Config.getUrl(), h2Config.getUsername(), h2Config.getPassword(), h2Config.getDriverClassName());

        routingDataSource.setDefaultTargetDataSource(postgresDataSource);
        routingDataSource.setTargetDataSources(Map.of("postgres", postgresDataSource, "h2", h2DataSource));

        return routingDataSource;
    }

    private DataSource createDataSource(String url, String username, String password, String driverClassName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
