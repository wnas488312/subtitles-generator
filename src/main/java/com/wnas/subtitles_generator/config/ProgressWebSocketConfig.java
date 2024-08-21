package com.wnas.subtitles_generator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ProgressWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    public static final String TOPIC_ENDPOINT = "/subject/%s";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/progress")
                .withSockJS();
    }
}
