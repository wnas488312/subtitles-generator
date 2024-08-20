package com.wnas488312.subtitles_generator.business.converters;

import com.wnas488312.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas488312.subtitles_generator.data.entity.TextChunk;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts DB text chunk entity into API object.
 */
@Component
public class TextChunksToSubtitlesTextChunkConverter implements Converter<TextChunk, SubtitlesTextChunk> {

    @Override
    public SubtitlesTextChunk convert(TextChunk source) {
        return new SubtitlesTextChunk(
                source.getText(),
                source.getStartFrame(),
                source.getEndFrame()
        );
    }
}
