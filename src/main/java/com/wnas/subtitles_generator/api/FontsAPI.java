package com.wnas.subtitles_generator.api;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API used to download custom font file previously loaded on server side.
 */
@RequestMapping("/fonts")
public interface FontsAPI {

    /**
     * Gets custom font file for given font name
     * @param fontName  Name of a font.
     * @return          Font file.
     */
    @GetMapping("/{fontName}")
    ResponseEntity<Resource> getFont(@PathVariable String fontName);
}
