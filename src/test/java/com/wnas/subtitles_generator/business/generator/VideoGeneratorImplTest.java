package com.wnas.subtitles_generator.business.generator;

import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.business.provider.FFmpegProvider;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.service.ProgressService;
import com.wnas.subtitles_generator.business.service.message.GenerationProgressStage;
import com.wnas.subtitles_generator.config.properties.AppConfig;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;
import com.wnas.subtitles_generator.testData.TestData;
import com.wnas.subtitles_generator.testData.dummyclasses.DummyFFmpegFrameGrabber;
import com.wnas.subtitles_generator.testData.dummyclasses.DummyFFmpegFrameRecorder;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoGeneratorImplTest {
    private static final Long SUBTITLES_ID = 1L;

    @Mock
    private AppConfig config;
    @Mock
    private FileService fileService;
    @Mock
    private ProgressService progressService;
    @Mock
    private FFmpegProvider fFmpegProvider;

    @InjectMocks
    private VideoGeneratorImpl videoGenerator;

    private final List<File> filesToDelete = new ArrayList<>();

    @AfterEach
    public void cleanUp() {
        filesToDelete.forEach(FileUtils::deleteQuietly);
    }


    @Test
    void generateVideoFromImagesTest() throws Exception {
        setUpMocksForVideoFromImages("H264");

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, false);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);
        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());

        videoGenerator.generateVideoFromImages(createContext());

        Assertions.assertThat(recorder.getStartCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getRecordCalledAmount().get()).isEqualTo(202); // 2 images for 100 frames each
        Assertions.assertThat(recorder.getStopCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getReleaseCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getExpectedVideoCodec().get()).isEqualTo(avcodec.AV_CODEC_ID_H264);

        verify(progressService, times(2)).updateProgress(eq(SUBTITLES_ID), eq(GenerationProgressStage.SUBTITLES), anyInt());
        verify(fileService, times(1)).createFile(eq(SUBTITLES_ID), eq(VideoFileType.SUBTITLES), eq("subtitles-original.mp4"));
    }

    @Test
    void generateVideoFromImages_H265CodecTest() throws Exception {
        setUpMocksForVideoFromImages("H265");

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, false);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);
        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());

        videoGenerator.generateVideoFromImages(createContext());

        Assertions.assertThat(recorder.getExpectedVideoCodec().get()).isEqualTo(avcodec.AV_CODEC_ID_H265);
    }

    @Test
    void generateVideoFromImages_unknownCodecTest() throws Exception {
        setUpMocksForVideoFromImages("unknown");

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, false);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);
        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());

        videoGenerator.generateVideoFromImages(createContext());

        Assertions.assertThat(recorder.getExpectedVideoCodec().get()).isEqualTo(avcodec.AV_CODEC_ID_NONE);
    }

    @Test
    void generateVideoFromImages_exceptionThrown_expectErrorTest() throws Exception {
        setUpMocksForVideoFromImages("unknown");

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, true);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);

        assertThatThrownBy(() -> videoGenerator.generateVideoFromImages(createContext()))
                .isInstanceOf(FFmpegFrameRecorder.Exception.class)
                .hasMessageContaining("This is bad!");
    }

    @Test
    void combineOriginalVideoWithSubtitlesTest() throws Exception {
        setUpMocksForVideoCombined();

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, false);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);
        final DummyFFmpegFrameGrabber grabber = new DummyFFmpegFrameGrabber("nowhere.mp4", false);
        when(fFmpegProvider.getGrabber(anyString())).thenReturn(grabber);

        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());

        videoGenerator.combineOriginalVideoWithSubtitles(createContext());

        Assertions.assertThat(recorder.getStartCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getRecordCalledAmount().get()).isEqualTo(202); // 2 images for 100 frames each
        Assertions.assertThat(recorder.getRecordSamplesCalledAmount().get()).isEqualTo(202); // 2 images for 100 frames each
        Assertions.assertThat(recorder.getStopCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getReleaseCalledAmount().get()).isEqualTo(1);

        Assertions.assertThat(grabber.getStartCalledAmount().get()).isEqualTo(2);
        Assertions.assertThat(grabber.getStopCalledAmount().get()).isEqualTo(2);
        Assertions.assertThat(grabber.getReleaseCalledAmount().get()).isEqualTo(2);
        Assertions.assertThat(grabber.getGrabImageCalledAmount().get()).isEqualTo(203); // 2 images for 100 frames each
        Assertions.assertThat(grabber.getGrabSamplesCalledAmount().get()).isEqualTo(203); // 2 images for 100 frames each

        verify(progressService, atLeast(202)).updateProgress(eq(SUBTITLES_ID), eq(GenerationProgressStage.VIDEO), anyInt());
        verify(progressService, atLeast(202)).updateProgress(eq(SUBTITLES_ID), eq(GenerationProgressStage.AUDIO), anyInt());
        verify(fileService, times(1)).createFile(eq(SUBTITLES_ID), eq(VideoFileType.COMBINED), eq("result-white"));
    }

    @Test
    void combineOriginalVideoWithSubtitles_nullSamples_expectOkTest() throws Exception {
        setUpMocksForVideoCombined();

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, false);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);
        final DummyFFmpegFrameGrabber grabber = new DummyFFmpegFrameGrabber("nowhere.mp4", true);
        when(fFmpegProvider.getGrabber(anyString())).thenReturn(grabber);

        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());

        videoGenerator.combineOriginalVideoWithSubtitles(createContext());

        Assertions.assertThat(recorder.getStartCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getRecordCalledAmount().get()).isEqualTo(202); // 2 images for 100 frames each
        Assertions.assertThat(recorder.getRecordSamplesCalledAmount().get()).isEqualTo(0);
        Assertions.assertThat(recorder.getStopCalledAmount().get()).isEqualTo(1);
        Assertions.assertThat(recorder.getReleaseCalledAmount().get()).isEqualTo(1);

        Assertions.assertThat(grabber.getStartCalledAmount().get()).isEqualTo(2);
        Assertions.assertThat(grabber.getStopCalledAmount().get()).isEqualTo(2);
        Assertions.assertThat(grabber.getReleaseCalledAmount().get()).isEqualTo(2);
        Assertions.assertThat(grabber.getGrabImageCalledAmount().get()).isEqualTo(203); // 2 images for 100 frames each
        Assertions.assertThat(grabber.getGrabSamplesCalledAmount().get()).isEqualTo(203); // 2 images for 100 frames each

        verify(progressService, atLeast(202)).updateProgress(eq(SUBTITLES_ID), eq(GenerationProgressStage.VIDEO), anyInt());
        verify(progressService, atLeast(1)).updateProgress(eq(SUBTITLES_ID), eq(GenerationProgressStage.AUDIO), eq(100));
        verify(fileService, times(1)).createFile(eq(SUBTITLES_ID), eq(VideoFileType.COMBINED), eq("result-white"));
    }

    @Test
    void combineOriginalVideoWithSubtitles_exceptionWhenStartingRecorder_expectErrorTest() throws Exception {
        setUpMocksForVideoCombined();

        final DummyFFmpegFrameRecorder recorder = new DummyFFmpegFrameRecorder("nowhere.mp4", 1000, 1000, true);
        when(fFmpegProvider.getRecorder(anyString(), anyInt(), anyInt())).thenReturn(recorder);
        final DummyFFmpegFrameGrabber grabber = new DummyFFmpegFrameGrabber("nowhere.mp4", false);
        when(fFmpegProvider.getGrabber(anyString())).thenReturn(grabber);

        assertThatThrownBy(() -> videoGenerator.combineOriginalVideoWithSubtitles(createContext()))
                .isInstanceOf(FFmpegFrameRecorder.Exception.class)
                .hasMessageContaining("This is bad!");
    }

    private void setUpMocksForVideoFromImages(String videoCodecName) throws IOException {
        final File tempVideoFile = File.createTempFile("temp", ".tmp");
        filesToDelete.add(tempVideoFile);
        when(fileService.createFile(anyLong(), any(VideoFileType.class), anyString())).thenReturn(tempVideoFile);
        when(config.getFrameRate()).thenReturn(24);
        when(config.getVideoCodecName()).thenReturn(videoCodecName);
    }

    private void setUpMocksForVideoCombined() throws IOException {
        when(fileService.getFile(anyLong(), any(VideoType.class))).thenReturn(TestData.videoFileEntity());

        final File tempVideoFile = File.createTempFile("temp", ".tmp");
        filesToDelete.add(tempVideoFile);
        when(fileService.createFile(anyLong(), any(VideoFileType.class), anyString())).thenReturn(tempVideoFile);
        when(config.getVideoCodecName()).thenReturn("H264");
    }

    private BufferedImage createImage(String fileName) throws IOException {
        try (InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName))) {
            return ImageIO.read(in);
        }
    }

    private VideoGeneratorProcessingContext createContext() throws IOException {
        BufferedImage image1 = createImage("image1.png");
        VideoGeneratorProcessingContext.ImageWithFrameCoordinates coordinates1 = new VideoGeneratorProcessingContext.ImageWithFrameCoordinates(image1, 0, 100);
        BufferedImage image2 = createImage("image2.png");
        VideoGeneratorProcessingContext.ImageWithFrameCoordinates coordinates2 = new VideoGeneratorProcessingContext.ImageWithFrameCoordinates(image2, 100, 200);

        return new VideoGeneratorProcessingContext(
                SUBTITLES_ID,
                "original.mp4",
                1080,
                720,
                List.of(coordinates1, coordinates2)
        );
    }
}