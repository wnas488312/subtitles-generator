package com.wnas488312.subtitles_generator.api;

import com.wnas488312.subtitles_generator.api.model.VideoType;
import com.wnas488312.subtitles_generator.api.model.response.BasicOkResponse;
import com.wnas488312.subtitles_generator.config.UploaderWebSocketConfig;
import com.wnas488312.subtitles_generator.websocket.handler.FileUploadWebSocketHandler;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * API used to handle video files.
 */
@RequestMapping("/video")
public interface VideoApi {

    /**
     * Rest endpoint used to upload original video file to the server.
     * @deprecated
     * This endpoint si replaced with Web socket. See {@link UploaderWebSocketConfig} and {@link FileUploadWebSocketHandler} to get more info
     * @param id    Identifier of subtitles generation process.
     * @param file  File to upload
     * @return      Basic OK response
     */
    @PostMapping("/{id}/original")
    BasicOkResponse uploadOriginalFile(@PathVariable Long id, @RequestParam("file") MultipartFile file);

    /**
     * Endpoint used to download video (generated or original)
     * @param id    Identifier of subtitles generation process.
     * @param type  Type of file to download (ORIGINAL, SUBTITLES or COMBINED)
     * @return      Response entity with file content .
     * @throws IOException  If file content cannot be read.
     */
    @GetMapping("/{id}/{type}/download")
    ResponseEntity<Resource> downloadFile(@PathVariable Long id, @PathVariable VideoType type) throws IOException;
}
