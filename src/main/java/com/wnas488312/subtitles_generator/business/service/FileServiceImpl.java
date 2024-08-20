package com.wnas488312.subtitles_generator.business.service;

import com.wnas488312.subtitles_generator.api.model.VideoType;
import com.wnas488312.subtitles_generator.config.properties.AppConfig;
import com.wnas488312.subtitles_generator.data.SubtitlesRepo;
import com.wnas488312.subtitles_generator.data.VideoFileRepo;
import com.wnas488312.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas488312.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileType;
import com.wnas488312.subtitles_generator.exception.NotFoundException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
public class FileServiceImpl implements FileService{
    private final AppConfig config;
    private final VideoFileRepo fileRepo;
    private final SubtitlesRepo subtitlesRepo;
    private final Converter<VideoType, VideoFileType> videoTypeConverter;

    public FileServiceImpl(AppConfig config, VideoFileRepo fileRepo, SubtitlesRepo subtitlesRepo, Converter<VideoType, VideoFileType> videoTypeConverter) {
        this.config = config;
        this.fileRepo = fileRepo;
        this.subtitlesRepo = subtitlesRepo;
        this.videoTypeConverter = videoTypeConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File createFile(Long subtitlesId, VideoFileType fileType, @Nullable String fileName) throws IOException {
        if (!subtitlesRepo.existsById(subtitlesId)) {
            throw notFound(subtitlesId);
        }

        File videoFile = File.createTempFile(
                config.getTempFilePrefix(),
                String.format(".%s", config.getVideoFormat())
        );

        VideoFileEntity entity = new VideoFileEntity();
        entity.setFilePath(videoFile.getPath());
        entity.setFileType(fileType);
        entity.setSubtitlesId(subtitlesId);

        if (StringUtils.isNotBlank(fileName)) {
            entity.setFileName(fileName);
        }

        fileRepo.save(entity);
        return videoFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFiles(Long subtitlesId) throws IOException {
        for (VideoFileEntity entity: fileRepo.findBySubtitlesId(subtitlesId)) {
            File fileToRemove = new File(entity.getFilePath());
            FileUtils.delete(fileToRemove);
            fileRepo.delete(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoFileEntity getFile(Long subtitlesId, VideoType fileType) {
        VideoFileType videoFileType = videoTypeConverter.convert(fileType);

        return fileRepo.findBySubtitlesId(subtitlesId).stream()
                .filter(file -> videoFileType.equals(file.getFileType()))
                .findFirst()
                .orElseThrow(() -> notFound(subtitlesId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFilesOlderThanOneDay() throws IOException {
        Collection<SubtitlesEntity> entriesOlderThanOneDay = subtitlesRepo.findEntriesOlderThanOneDay();
        log.info("Found {} entries to remove", entriesOlderThanOneDay.size());
        for (SubtitlesEntity entry: entriesOlderThanOneDay) {
            Set<VideoFileEntity> bySubtitlesId = fileRepo.findBySubtitlesId(entry.getId());
            for (VideoFileEntity fileEntry: bySubtitlesId) {
                File fileToRemove = new File(fileEntry.getFilePath());
                log.info("Removing file {} for id: {}", fileEntry.getFileName(), entry.getId());
                FileUtils.delete(fileToRemove);
            }
            fileRepo.deleteAll(bySubtitlesId);
        }
        subtitlesRepo.deleteAll(entriesOlderThanOneDay);
    }

    private static RuntimeException notFound(Long id) {
        return new NotFoundException(String.format("Subtitles entry for id %s not found.", id));
    }
}
