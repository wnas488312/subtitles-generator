package com.wnas488312.subtitles_generator.business.converters;

import com.wnas488312.subtitles_generator.api.model.VideoType;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts API video type enum into DB enum.
 */
@Component
public class VideoTypeToVideoFileTypeConverter implements Converter<VideoType, VideoFileType> {

    @Override
    public VideoFileType convert(VideoType source) {
        switch (source) {
            case ORIGINAL -> {
                return VideoFileType.ORIGINAL;
            }
            case SUBTITLES -> {
                return VideoFileType.SUBTITLES;
            }
            case COMBINED -> {
                return VideoFileType.COMBINED;
            }
        }
        return null;
    }
}
