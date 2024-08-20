package com.wnas488312.subtitles_generator.business.service;

import com.wnas488312.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas488312.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas488312.subtitles_generator.data.SubtitlesRepo;
import com.wnas488312.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas488312.subtitles_generator.data.entity.TextChunk;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import com.wnas488312.subtitles_generator.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SubtitlesServiceImpl implements SubtitlesService {
    private final SubtitlesRepo repo;
    private final FileService fileService;
    private final Converter<SubtitlesTextChunk, TextChunk> textChunksConverter;

    public SubtitlesServiceImpl(SubtitlesRepo repo, FileService fileService, Converter<SubtitlesTextChunk, TextChunk> textChunksConverter) {
        this.repo = repo;
        this.textChunksConverter = textChunksConverter;
        this.fileService = fileService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createNewDbEntry() {
        SubtitlesEntity newEntry = new SubtitlesEntity();
        newEntry.setStatus(VideoFileStatus.QUEUED);

        LocalDateTime now = LocalDateTime.now();
        newEntry.setCreationDate(now);
        return repo.save(newEntry).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubtitlesEntity updateSubtitlesPropertiesFromRequest(Long id, UpdatePropertiesRequest request) {
        SubtitlesEntity old = getDbEntryById(id);

        if (!Objects.isNull(request.bottomMargin())) {
            old.setBottomMargin(request.bottomMargin());
        }

        if (!StringUtils.isBlank(request.fontName())) {
            old.setFontName(request.fontName());
        }

        if (!CollectionUtils.isEmpty(request.textChunks())) {
            List<TextChunk> textChunks = new ArrayList<>();
            request.textChunks().forEach(chunk -> {
                TextChunk textChunk = textChunksConverter.convert(chunk);
                textChunks.add(textChunk);
            });
            old.setTextChunks(textChunks);
        }

        return repo.save(old);
    }

    /**
     * {@inheritDoc}b
     */
    @Override
    public void removeSubtitles(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            try {
                fileService.removeFiles(id);
                return;
            } catch (IOException e) {
                String errorMessage = String.format("Error occurred when trying to delete files for subtitles with id: %s", id);
                log.error(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

        throw notFound(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubtitlesEntity getDbEntryById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> notFound(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStage(Long entityId, VideoFileStatus status) {
        repo.findById(entityId).ifPresent(entity -> {
            entity.setStatus(status);
            repo.save(entity);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStageToError(Long entityId, String errorMessage) {
        repo.findById(entityId).ifPresent(entity -> {
            entity.setStatus(VideoFileStatus.ERROR);
            entity.setErrorMessage(errorMessage);
            repo.save(entity);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubtitlesEntity saveEntry(SubtitlesEntity entry) {
        return repo.save(entry);
    }

    private static RuntimeException notFound(Long id) {
        return new NotFoundException(String.format("Subtitles entry for id %s not found.", id));
    }
}
