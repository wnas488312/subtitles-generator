package com.wnas.subtitles_generator.api;

import com.wnas.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas.subtitles_generator.api.model.response.BasicOkResponse;
import com.wnas.subtitles_generator.api.model.response.CreateVideoResponse;
import com.wnas.subtitles_generator.api.model.response.GetVideoStatusResponse;
import com.wnas.subtitles_generator.api.model.response.UpdatePropertiesResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Main API of this application. Used to interact with process of subtitles
 * generation (initialise it, start, get status etc.)
 */
@RequestMapping("/subtitles")
public interface SubtitlesApi {

    /**
     * Initialises the process of subtitles generation.
     * Creates new DB entry and sets it to queued stage.
     * @return Object with identifier of newly created subtitles.
     */
    @PostMapping("/initialize")
    CreateVideoResponse initialiseProcess();

    /**
     * Sets or updates and stores properties needed in subtitles generation process.
     * @param id        Identifier of subtitles process.
     * @param request   Request object containing properties to update.
     * @return          Object with updated properties.
     */
    @PutMapping("/{id}")
    UpdatePropertiesResponse updateSubtitlesProperties(
            @PathVariable Long id,
            @RequestBody UpdatePropertiesRequest request
    );

    /**
     * Starts the processing of subtitles.
     * Properties needed to generate subtitles needs to be set before.
     * Generation process is done asynchronously, so this endpoint will
     * respond before generation will be completed.
     * Progress of the process is exposed through web socket.
     * @param id    Identifier of subtitles process.
     * @return      Basic OK response
     */
    @PutMapping("/{id}/process")
    BasicOkResponse processSubtitles(@PathVariable Long id);

    /**
     * Gets current stage of the process and error message if in error stage.
     * @param id    Identifier of subtitles process.
     * @return      Status and error message if needed.
     */
    @GetMapping("/{id}")
    GetVideoStatusResponse getVideoStatus(@PathVariable Long id);

    /**
     * Deletes subtitles process, including database entries and
     * files uploaded/generated during the process.
     * @param id    Identifier of subtitles process.
     * @return      Basic OK response
     */
    @DeleteMapping("/{id}")
    BasicOkResponse deleteVideo(@PathVariable Long id);
}
