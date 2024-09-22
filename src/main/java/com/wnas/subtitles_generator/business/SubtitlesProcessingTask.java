package com.wnas.subtitles_generator.business;

import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.generator.ImageGenerator;
import com.wnas.subtitles_generator.business.generator.VideoGenerator;
import com.wnas.subtitles_generator.business.generator.VideoGeneratorProcessingContext;
import com.wnas.subtitles_generator.business.service.ProgressServiceImpl;
import com.wnas.subtitles_generator.business.service.SubtitlesService;
import com.wnas.subtitles_generator.business.service.message.GenerationProgressStage;
import com.wnas.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas.subtitles_generator.data.entity.TextChunk;
import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Setter
@Slf4j
public class SubtitlesProcessingTask extends Thread{
    private VideoGenerator videoGenerator;
    private SubtitlesService subtitlesService;
    private FileService fileService;
    private ProgressServiceImpl progressService;
    private Long subtitlesId;

    @Override
    public void run() {
        log.info("Processing of subtitles with id: {} started", subtitlesId);
        subtitlesService.setStage(subtitlesId, VideoFileStatus.PROCESSING);

        final List<VideoGeneratorProcessingContext.ImageWithFrameCoordinates> images = new ArrayList<>();

        VideoFileEntity originalFile = fileService.getFile(subtitlesId, VideoType.ORIGINAL);
        SubtitlesEntity entry = setHeightAndWidth(originalFile);

        log.info("Generating images for subtitles with id: {}", subtitlesId);
        log.info("Number of images to generate for subtitles with id {}: {}", subtitlesId, entry.getTextChunks().size());
        int index = 0;
        List<TextChunk> textChunks = entry.getTextChunks();
        for (TextChunk chunk: textChunks) {
            log.info("Generating image nr {} for subtitles with id: {}", index, subtitlesId);
            index++;
            final VideoGeneratorProcessingContext.ImageWithFrameCoordinates imageWithFrameCoordinates = getImageWithFrameCoordinates(entry, chunk);
            images.add(imageWithFrameCoordinates);
            float imageProcessingProgress = ((float) index / entry.getTextChunks().size()) * 100;
            progressService.updateProgress(subtitlesId, GenerationProgressStage.IMAGES, (int) imageProcessingProgress);
        }

        log.info("Generating images for subtitles with id: {} finished", subtitlesId);
        log.info("Generating subtitles video for subtitles with id: {}", subtitlesId);
        try {
            VideoGeneratorProcessingContext data = new VideoGeneratorProcessingContext(
                    subtitlesId,
                    originalFile.getFileName(),
                    entry.getWidth(),
                    entry.getHeight(),
                    images
            );

            videoGenerator.generateVideoFromImages(data);
            log.info("Generating subtitles video for subtitles with id: {} finished", subtitlesId);
            log.info("Generating combined video for subtitles with id: {}", subtitlesId);
            videoGenerator.combineOriginalVideoWithSubtitles(data);
            log.info("Generating combined video for subtitles with id: {} finished", subtitlesId);

            progressService.updateProgressDone(subtitlesId);
            subtitlesService.setStage(subtitlesId, VideoFileStatus.READY);
            log.info("Processing of subtitles with id: {} finished successfully", subtitlesId);
        } catch (Exception e) {
            String errorMessage = String.format("Error occurred when generating subtitles for id %s: %s", subtitlesId, e.getMessage());
            handleError(subtitlesId, e, errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private SubtitlesEntity setHeightAndWidth(VideoFileEntity originalFile) {
        try (FFmpegFrameGrabber backgroundGrabber = new FFmpegFrameGrabber(originalFile.getFilePath())) {
            backgroundGrabber.start();

            int width = backgroundGrabber.getImageWidth();
            int height = backgroundGrabber.getImageHeight();

            backgroundGrabber.stop();

            SubtitlesEntity entry = subtitlesService.getDbEntryById(subtitlesId);
            entry.setWidth(width);
            entry.setHeight(height);
            return subtitlesService.saveEntry(entry);
        } catch (FrameGrabber.Exception e) {
            String errorMessage = String.format("Error occurred when getting height and width for subtitles for id %s: %s", subtitlesId, e.getMessage());
            handleError(subtitlesId, e, errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private void handleError(Long subtitlesId, Exception e, String errorMessage) {
        log.error(errorMessage);
        progressService.updateProgressProcessFailed(subtitlesId);
        subtitlesService.setStageToError(subtitlesId, errorMessage);
    }

    private static VideoGeneratorProcessingContext.ImageWithFrameCoordinates getImageWithFrameCoordinates(SubtitlesEntity entry, TextChunk chunk) {
        final ImageGenerator imageGenerator = new ImageGenerator(
                chunk.getText(),
                entry.getFontName(),
                entry.getWidth(),
                entry.getHeight(),
                entry.getBottomMargin(),
                entry.getFontSize(),
                new Color(entry.getColor().getR(), entry.getColor().getG(), entry.getColor().getB())
        );
        final BufferedImage image = imageGenerator.generateImage();
        return new VideoGeneratorProcessingContext.ImageWithFrameCoordinates(image, chunk.getStartFrame(), chunk.getEndFrame());
    }
}
