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

        try (FFmpegFrameRecorder recorder = createRecorder(videoFile.getPath(), data.width(), data.height(), config.getFrameRate())) {
            recorder.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();
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
            recorder.release();
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

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(originalFile.getFilePath())) {
            frameGrabber.start();

            int width = frameGrabber.getImageWidth();
            int height = frameGrabber.getImageHeight();
            int frameRate = (int) frameGrabber.getFrameRate();
            int videoBitrate = frameGrabber.getVideoBitrate();
            double durationInMicroseconds = frameGrabber.getLengthInTime() / 1_000_000.0;

            try (FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(videoFile.getPath(), width, height);
                 Java2DFrameConverter converter = new Java2DFrameConverter()) {
                frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                frameRecorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                frameRecorder.setFrameRate(frameRate);
                frameRecorder.setVideoBitrate(videoBitrate);
                frameRecorder.setFormat("mp4");

                frameRecorder.start();
                Frame frame;

                int currentFrameNumber = 0;
                while ((frame = frameGrabber.grabImage()) != null) {
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

                frameGrabber.stop();
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

    private FFmpegFrameRecorder createRecorder(String filePath, int width, int height, double frameRate, Integer bitRate) {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(filePath, width, height);
        recorder.setFrameRate(frameRate);
        recorder.setVideoCodecName(config.getVideoCodecName());
        recorder.setFormat(config.getVideoFormat());
        recorder.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P);
        if (!Objects.isNull(bitRate)) {
            recorder.setVideoBitrate(bitRate);
        }
        return recorder;
    }
}
