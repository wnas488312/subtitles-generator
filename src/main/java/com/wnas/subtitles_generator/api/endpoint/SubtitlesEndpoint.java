package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.api.SubtitlesApi;
import com.wnas.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas.subtitles_generator.api.model.response.BasicOkResponse;
import com.wnas.subtitles_generator.api.model.response.CreateVideoResponse;
import com.wnas.subtitles_generator.api.model.response.GetVideoStatusResponse;
import com.wnas.subtitles_generator.api.model.response.UpdatePropertiesResponse;
import com.wnas.subtitles_generator.business.SubtitlesProcessingTask;
import com.wnas.subtitles_generator.business.builder.SubtitlesProcessingTaskBuilder;
import com.wnas.subtitles_generator.business.service.SubtitlesService;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.generator.VideoGenerator;
import com.wnas.subtitles_generator.business.service.ProgressServiceImpl;
import com.wnas.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas.subtitles_generator.data.entity.TextChunk;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
public class SubtitlesEndpoint implements SubtitlesApi {
    private final SubtitlesService service;
    private final FileService fileService;
    private final SubtitlesService subtitlesService;
    private final VideoGenerator videoGenerator;
    private final ExecutorService executorService;
    private final ProgressServiceImpl progressService;
    private final Converter<TextChunk, SubtitlesTextChunk> textChunkConverter;

    public SubtitlesEndpoint(
            SubtitlesService service,
            FileService fileService,
            SubtitlesService subtitlesService,
            VideoGenerator videoGenerator,
            ProgressServiceImpl progressService,
            Converter<TextChunk, SubtitlesTextChunk> textChunkConverter
    ) {
        this.service = service;
        this.fileService = fileService;
        this.subtitlesService = subtitlesService;
        this.videoGenerator = videoGenerator;
        this.progressService = progressService;
        this.textChunkConverter = textChunkConverter;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateVideoResponse initialiseProcess() {
        Long newDbEntryId = service.createNewDbEntry();
        log.info("Initialised process of subtitles generation for id: {}", newDbEntryId);
        return new CreateVideoResponse(newDbEntryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdatePropertiesResponse updateSubtitlesProperties(Long id, UpdatePropertiesRequest request) {
        log.info("Updating subtitles properties for id: {}", id);

        SubtitlesEntity subtitlesEntity = service.updateSubtitlesPropertiesFromRequest(id, request);
        List<SubtitlesTextChunk> chunks = new ArrayList<>();
        subtitlesEntity.getTextChunks()
                .forEach(chunk ->
                        chunks.add(textChunkConverter.convert(chunk))
                )
        ;

        return new UpdatePropertiesResponse(
                subtitlesEntity.getId(),
                subtitlesEntity.getWidth(),
                subtitlesEntity.getHeight(),
                subtitlesEntity.getBottomMargin(),
                subtitlesEntity.getFontName(),
                chunks,
                subtitlesEntity.getCreationDate()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BasicOkResponse processSubtitles(Long id) {
        log.info("Starting processing of subtitles generation for id: {}", id);

        SubtitlesProcessingTask processor = new SubtitlesProcessingTaskBuilder()
                .withVideoGenerator(videoGenerator)
                .withFileService(fileService)
                .withSubtitlesService(subtitlesService)
                .withProgressService(progressService)
                .withSubtitlesId(id)
                .build();

        executorService.submit(processor);

        return new BasicOkResponse("OK");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetVideoStatusResponse getVideoStatus(Long id) {
        SubtitlesEntity entry = service.getDbEntryById(id);

        if (VideoFileStatus.ERROR.equals(entry.getStatus())) {
            return new GetVideoStatusResponse(
                    VideoFileStatus.ERROR,
                    entry.getErrorMessage()
            );
        }

        return new GetVideoStatusResponse(
                entry.getStatus(),
                null
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BasicOkResponse deleteVideo(Long id) {
        log.info("Deleting subtitles for id: {}", id);
        service.removeSubtitles(id);
        return new BasicOkResponse("OK");
    }
}
