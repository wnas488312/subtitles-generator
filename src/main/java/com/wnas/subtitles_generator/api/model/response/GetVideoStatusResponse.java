package com.wnas.subtitles_generator.api.model.response;

import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;

/**
 * Response for endpoint for getting status of subtitles generation process.
 * @param status        Status of generation process.
 * @param errorMessage  Details about error if one occurred during processing.
 *                      Only present if in ERROR stage.
 */
public record GetVideoStatusResponse(
        VideoFileStatus status,
        String errorMessage
) {
}
