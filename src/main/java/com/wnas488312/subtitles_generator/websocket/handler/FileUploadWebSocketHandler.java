package com.wnas488312.subtitles_generator.websocket.handler;

import com.wnas488312.subtitles_generator.business.service.FileService;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FileUploadWebSocketHandler extends BinaryWebSocketHandler {
    private final FileService service;
    private final Map<Long, Path> filesPathsById = new HashMap<>();

    public FileUploadWebSocketHandler(FileService service) {
        super();
        this.service = service;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long subtitlesId;
        try {
            subtitlesId = getIdFromSession(session);

            final String fileName = getFileNameFromSession(session, subtitlesId);
            final File tempFile = service.createFile(subtitlesId, VideoFileType.ORIGINAL, fileName);

            filesPathsById.put(subtitlesId, tempFile.toPath());
        } catch (RuntimeException e) {
            return;
        }

        log.info("Started upload for original file for subtitles id: {}", subtitlesId);
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        Long subtitlesId;
        try {
            subtitlesId = getIdFromSession(session);
        } catch (RuntimeException e) {
            return;
        }

        byte[] data = message.getPayload().array();
        Path filePath = filesPathsById.get(subtitlesId);
        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            outputStream.write(data);
        }
    }

    private String getFileNameFromSession(WebSocketSession session, Long subtitlesId) {
        try {
            URI uri = getUriFromSession(session);
            if (StringUtils.isBlank(uri.getQuery())) {
                String errorMessage = String.format("Query not found in URI for id: %s. Cannot retrieve file name", subtitlesId);
                log.warn(errorMessage);
                session.close(CloseStatus.BAD_DATA);
                throw new RuntimeException(errorMessage);
            }

            Map<String, String> queryParams = getQueryParamsFromUri(uri);
            if (queryParams.containsKey("fileName")) {
                return queryParams.get("fileName");
            }
            String errorMessage = String.format("Query does not contain file name for id: %s", subtitlesId);
            log.warn(errorMessage);
            session.close(CloseStatus.BAD_DATA);
            throw new RuntimeException(errorMessage);
        } catch (IOException e) {
            String message = String.format("Cannot close socket for file upload for id: %s", subtitlesId);
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private Long getIdFromSession(WebSocketSession session) {
        try {
            URI uri = getUriFromSession(session);
            try {
                String path = uri.getPath();
                String id = path.split("/video/original/")[1];
                return Long.parseLong(id);
            } catch (NumberFormatException e) {
                String message = "Subtitles id should be a number";
                log.warn(message, e);
                session.close(CloseStatus.BAD_DATA);
                throw new RuntimeException(message, e);
            }
        } catch (IOException e) {
            String message = "Cannot close socket for file upload";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private Map<String, String> getQueryParamsFromUri(URI uri) {
        return Arrays.stream(uri.getQuery().split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }

    private URI getUriFromSession(WebSocketSession session) throws IOException {
        URI uri = session.getUri();
        if (uri == null) {
            String message = "WebSocket connection closed due to null URI";
            log.warn(message);
            session.close(CloseStatus.BAD_DATA);
            throw new RuntimeException(message);
        }
        return uri;
    }
}
