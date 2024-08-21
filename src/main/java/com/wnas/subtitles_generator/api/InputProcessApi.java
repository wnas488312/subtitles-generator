package com.wnas.subtitles_generator.api;

import com.wnas.subtitles_generator.api.model.request.InputProcessRequest;
import com.wnas.subtitles_generator.api.model.response.InputProcessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * API used to process user text input.
 */
@RequestMapping("/processInput")
public interface InputProcessApi {

    /**
     * Splits provided text input into smaller chunks of text,
     * based on properties also provided with request.
     * @param request   Properties of text split with text to split.
     * @return          Object with list of split text inside.
     */
    @PostMapping
    @ResponseBody
    InputProcessResponse processInput(@RequestBody InputProcessRequest request);
}
