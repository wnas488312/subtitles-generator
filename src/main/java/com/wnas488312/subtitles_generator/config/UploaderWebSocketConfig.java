package com.wnas488312.subtitles_generator.config;

import com.wnas488312.subtitles_generator.business.service.FileService;
import com.wnas488312.subtitles_generator.websocket.handler.FileUploadWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Component
public class UploaderWebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private FileService service;

    private static final String ENDPOINT_PATH = "/video/original/{id}";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new FileUploadWebSocketHandler(service), ENDPOINT_PATH)
                .setAllowedOrigins("*");
    }
}
