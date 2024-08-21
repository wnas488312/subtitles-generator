package com.wnas.subtitles_generator.business.builder;

import com.wnas.subtitles_generator.business.SubtitlesProcessingTask;
import com.wnas.subtitles_generator.business.generator.VideoGenerator;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.service.ProgressServiceImpl;
import com.wnas.subtitles_generator.business.service.SubtitlesService;
import lombok.NoArgsConstructor;

/**
 * Builder class used to create an instance of SubtitlesProcessingTask
 */
@NoArgsConstructor
public class SubtitlesProcessingTaskBuilder {
    private VideoGenerator videoGenerator;
    private SubtitlesService subtitlesService;
    private FileService fileService;
    private ProgressServiceImpl progressService;
    private Long subtitlesId;

    public SubtitlesProcessingTaskBuilder withVideoGenerator(VideoGenerator videoGenerator) {
        this.videoGenerator = videoGenerator;
        return this;
    }

    public SubtitlesProcessingTaskBuilder withSubtitlesService(SubtitlesService subtitlesService) {
        this.subtitlesService = subtitlesService;
        return this;
    }

    public SubtitlesProcessingTaskBuilder withFileService(FileService fileService) {
        this.fileService = fileService;
        return this;
    }

    public SubtitlesProcessingTaskBuilder withProgressService(ProgressServiceImpl progressService) {
        this.progressService = progressService;
        return this;
    }

    public SubtitlesProcessingTaskBuilder withSubtitlesId(Long subtitlesId) {
        this.subtitlesId = subtitlesId;
        return this;
    }

    /**
     * Builds SubtitlesProcessingTask based on previously provided properties
     * @return Built SubtitlesProcessingTask
     */
    public SubtitlesProcessingTask build() {
        SubtitlesProcessingTask processor = new SubtitlesProcessingTask();
        processor.setVideoGenerator(videoGenerator);
        processor.setFileService(fileService);
        processor.setSubtitlesService(subtitlesService);
        processor.setProgressService(progressService);
        processor.setSubtitlesId(subtitlesId);
        return processor;
    }
}

