package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.api.VideoApi;
import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.api.model.response.BasicOkResponse;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.config.properties.AppConfig;
import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;
import com.wnas.subtitles_generator.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@RestController
@Slf4j
public class VideoEndpoint implements VideoApi {
    private final AppConfig config;
    private final FileService service;

    public VideoEndpoint(AppConfig config, FileService service) {
        this.config = config;
        this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public BasicOkResponse uploadOriginalFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            File tempFile = service.createFile(id, VideoFileType.ORIGINAL, file.getOriginalFilename());
            file.transferTo(tempFile);
        } catch (IOException e) {
            String errorMessage = String.format("Error occurred when processing original video for id %s", id);
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }

        return BasicOkResponse.ok();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, @PathVariable VideoType type) throws IOException {
        if (Objects.isNull(id)) {
            throw new BadRequestException("Subtitles identifier cannot be null");
        }

        if (Objects.isNull(type)) {
            throw new BadRequestException("Video file type cannot be null");
        }

        VideoFileEntity entity = service.getFile(id, type);
        File file = new File(entity.getFilePath());

        if (!file.exists()) {
            throw new RuntimeException(String.format("Subtitles with id %s exists in database, but file cannot be found.", id));
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s.%s", entity.getFileName(), config.getVideoFormat()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
}
