package com.wnas.subtitles_generator.api;

import com.wnas.subtitles_generator.api.model.response.DefaultPropertiesResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * API used to get default properties for subtitles visual properties, like font or bottom margin.
 */
@RequestMapping("/properties")
public interface DefaultPropertiesApi {

    /**
     * Responds with default subtitles properties taken from configuration with available fonts names.
     * @return  Default properties.
     */
    @GetMapping("/default")
    @ResponseBody
    DefaultPropertiesResponse getDefaultProperties();
}
