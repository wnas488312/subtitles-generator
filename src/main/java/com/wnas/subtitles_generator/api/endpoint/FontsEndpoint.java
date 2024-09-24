package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.api.FontsAPI;
import com.wnas.subtitles_generator.business.service.FontsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;

@Slf4j
@RestController
public class FontsEndpoint implements FontsAPI {
    private final FontsService fontsService;

    public FontsEndpoint(FontsService fontsService) {
        this.fontsService = fontsService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Resource> getFont(@PathVariable String fontName) {
        Path path = new File(fontsService.getCustomFontPath(fontName)).toPath();
        try {
            final Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            String errorMessage = String.format("Error occurred when trying to get font file for font name: %s", fontName);
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage);
        }
    }
}
