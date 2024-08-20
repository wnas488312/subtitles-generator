package com.wnas488312.subtitles_generator.business.converters;

import com.wnas488312.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas488312.subtitles_generator.data.entity.TextChunk;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts API text chunk object into DB entity.
 */
@Component
public class SubtitlesTextChunkToTextChunkConverter implements Converter<SubtitlesTextChunk, TextChunk> {

    @Override
    public TextChunk convert(SubtitlesTextChunk source) {
        TextChunk chunk = new TextChunk();
        chunk.setText(source.text());
        chunk.setStartFrame(source.startFrame());
        chunk.setEndFrame(source.endFrame());
        return chunk;
    }
}
