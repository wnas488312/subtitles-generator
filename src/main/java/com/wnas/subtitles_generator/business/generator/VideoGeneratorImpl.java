package com.wnas.subtitles_generator.business.generator;

import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.service.ProgressService;
import com.wnas.subtitles_generator.business.service.message.GenerationProgressStage;
import com.wnas.subtitles_generator.config.properties.AppConfig;
import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class VideoGeneratorImpl implements VideoGenerator{
    private final AppConfig config;
    private final FileService fileService;
    private final ProgressService progressService;

    public VideoGeneratorImpl(AppConfig config, FileService fileService, ProgressService progressService) {
        this.config = config;
        this.fileService = fileService;
        this.progressService = progressService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateVideoFromImages(VideoGeneratorProcessingContext data) throws Exception {
        File videoFile = fileService.createFile(
                data.dbVideoFileId(),
                VideoFileType.SUBTITLES,
                String.format("subtitles-%s", data.originalFileName())
        );

        try (FFmpegFrameRecorder recorder = createRecorder(videoFile.getPath(), data.width(), data.height(), config.getFrameRate());
             Java2DFrameConverter converter = new Java2DFrameConverter()) {
            recorder.start();

            int numberOfFramesToGenerate = data.imagesWithFrameCoordinates().getLast().endFrame();
            int currentFrame = 0;
            for (VideoGeneratorProcessingContext.ImageWithFrameCoordinates imageWithFrameCoordinates : data.imagesWithFrameCoordinates()) {
                Frame frame = converter.convert(imageWithFrameCoordinates.image());
                int bound = imageWithFrameCoordinates.endFrame();
                for (int ignored = imageWithFrameCoordinates.startFrame(); ignored <= bound; ignored++) {
                    currentFrame++;
                    recorder.record(frame);
                }
                float progress = ((float) currentFrame / numberOfFramesToGenerate) * 100;
                progressService.updateProgress(data.dbVideoFileId(), GenerationProgressStage.SUBTITLES, (int) progress);
            }

            recorder.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void combineOriginalVideoWithSubtitles(VideoGeneratorProcessingContext data, Long subtitlesId) throws Exception {
        VideoFileEntity originalFile = fileService.getFile(subtitlesId, VideoType.ORIGINAL);

        File videoFile = fileService.createFile(
                subtitlesId,
                VideoFileType.COMBINED,
                String.format("result-%s", originalFile.getFileName())
        );

        try (FFmpegFrameGrabber imageGrabber = new FFmpegFrameGrabber(originalFile.getFilePath());
             FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(originalFile.getFilePath())) {
            imageGrabber.start();
            audioGrabber.start();

            final int width = imageGrabber.getImageWidth();
            final int height = imageGrabber.getImageHeight();
            final double frameRate = imageGrabber.getFrameRate();
            final double durationInMicroseconds = imageGrabber.getLengthInTime() / 1_000_000.0;
            FFmpegLogCallback.set();

            try (FFmpegFrameRecorder frameRecorder = createRecorder(videoFile.getPath(), width, height, frameRate, imageGrabber);
                 Java2DFrameConverter converter = new Java2DFrameConverter()) {
                frameRecorder.start();
                Frame frame;

                int currentFrameNumber = 0;
                log.info("Generating image frames for combined video for id: {}", subtitlesId);
                while ((frame = imageGrabber.grabImage()) != null) {
                    log.debug("Combine frame {}/{}", currentFrameNumber, frameRate * durationInMicroseconds);
                    currentFrameNumber++;

                    BufferedImage bgImage = converter.convert(frame);
                    BufferedImage overlayImage = getImageInRange(currentFrameNumber, data.imagesWithFrameCoordinates());
                    BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

                    Graphics2D g = combinedImage.createGraphics();
                    g.drawImage(bgImage, 0, 0, Color.BLACK, null);

                    if (!Objects.isNull(overlayImage)) {
                        g.drawImage(overlayImage, 0, 0, null);
                    }
                    g.dispose();

                    Frame combinedFrame = converter.convert(Objects.isNull(overlayImage)? bgImage: combinedImage);
                    frameRecorder.record(combinedFrame);

                    double progress = (currentFrameNumber / (frameRate * durationInMicroseconds)) * 100;
                    if (progress >= 100) {
                        progress = 99;
                    }

                    progressService.updateProgress(data.dbVideoFileId(), GenerationProgressStage.VIDEO, (int) progress);
                }

                log.info("Generating audio for combined video for id: {}", subtitlesId);
                currentFrameNumber = 0;
                Frame sampleFrame;
                while ((sampleFrame = audioGrabber.grabSamples()) != null) {
                    if (sampleFrame.samples != null) {
                        log.debug("Combine audio frame {}/{}", currentFrameNumber, frameRate * durationInMicroseconds);
                        currentFrameNumber++;

                        frameRecorder.recordSamples(sampleFrame.samples);

                        double progress = (currentFrameNumber / (frameRate * durationInMicroseconds)) * 100;
                        if (progress >= 100) {
                            progress = 99;
                        }
                        progressService.updateProgress(data.dbVideoFileId(), GenerationProgressStage.AUDIO, (int) progress);
                    }
                }

                audioGrabber.stop();
                imageGrabber.stop();
                frameRecorder.stop();
            }
        }
    }

    private BufferedImage getImageInRange(int currentFrame, List<VideoGeneratorProcessingContext.ImageWithFrameCoordinates> imagesWithFrameCoordinates) {
        for (VideoGeneratorProcessingContext.ImageWithFrameCoordinates image: imagesWithFrameCoordinates) {
            if (currentFrame >= image.startFrame() && currentFrame < image.endFrame()) {
                return image.image();
            }
        }

        return null;
    }

    private FFmpegFrameRecorder createRecorder(String filePath, int width, int height, double frameRate) {
        return createRecorder(filePath, width, height, frameRate, null);
    }

    private FFmpegFrameRecorder createRecorder(String filePath, int width, int height, double frameRate, FFmpegFrameGrabber grabber) {
        final Integer videoBitrate = Objects.isNull(grabber)? null: grabber.getVideoBitrate();
        final Integer sampleRate =  Objects.isNull(grabber)? null: grabber.getSampleRate();
        final Integer audioChannels =  Objects.isNull(grabber)? null: grabber.getAudioChannels();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(filePath, width, height);
        recorder.setVideoCodec(getCodecByConfiguration());
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFrameRate(frameRate);
        recorder.setFormat(config.getVideoFormat());

        if (!Objects.isNull(grabber)) {
            recorder.setVideoBitrate(videoBitrate);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioChannels(audioChannels);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        }

        return recorder;
    }

    private int getCodecByConfiguration() {
        return switch (config.getVideoCodecName()) {
            case "H264" -> avcodec.AV_CODEC_ID_H264;
            case "H265" -> avcodec.AV_CODEC_ID_H265;
            default -> avcodec.AV_CODEC_ID_NONE;
        };
    }
}
