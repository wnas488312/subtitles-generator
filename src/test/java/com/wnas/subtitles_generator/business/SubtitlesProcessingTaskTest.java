package com.wnas.subtitles_generator.business;

import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.business.builder.SubtitlesProcessingTaskBuilder;
import com.wnas.subtitles_generator.business.generator.VideoGenerator;
import com.wnas.subtitles_generator.business.generator.VideoGeneratorProcessingContext;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.service.ProgressServiceImpl;
import com.wnas.subtitles_generator.business.service.SubtitlesService;
import com.wnas.subtitles_generator.business.service.message.GenerationProgressStage;
import com.wnas.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import com.wnas.subtitles_generator.testData.TestData;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtitlesProcessingTaskTest {

    private Long subtitlesId = 1L;
    @Mock
    private VideoGenerator videoGenerator;
    @Mock
    private SubtitlesService subtitlesService;
    @Mock
    private FileService fileService;
    @Mock
    private ProgressServiceImpl progressService;

    private List<File> filesToDelete = new ArrayList<>();

    @AfterEach
    public void cleanUp() {
        filesToDelete.forEach(FileUtils::deleteQuietly);
    }

    @Test
    void runTest() throws Exception {
        doNothing().when(subtitlesService).setStage(anyLong(), any(VideoFileStatus.class));

        final VideoFileEntity videoFileEntity = TestData.videoFileEntity();
        videoFileEntity.setFilePath(getExampleFilePath());
        when(fileService.getFile(eq(subtitlesId), eq(VideoType.ORIGINAL))).thenReturn(videoFileEntity);

        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(subtitlesService.getDbEntryById(eq(subtitlesId))).thenReturn(subtitles);
        when(subtitlesService.saveEntry(any(SubtitlesEntity.class)))
                .thenAnswer((Answer<SubtitlesEntity>) invocation -> (SubtitlesEntity) invocation.getArguments()[0]);

        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());
        doNothing().when(videoGenerator).generateVideoFromImages(any(VideoGeneratorProcessingContext.class));
        doNothing().when(progressService).updateProgressDone(anyLong());

        SubtitlesProcessingTask task = createTestedClass();
        task.run(); // I don't want to run it asynchronously in tests.

        verify(subtitlesService, times(1)).setStage(eq(subtitlesId), eq(VideoFileStatus.PROCESSING));
        verify(subtitlesService, times(1)).setStage(eq(subtitlesId), eq(VideoFileStatus.READY));
        verify(progressService, times(1)).updateProgress(eq(subtitlesId), eq(GenerationProgressStage.IMAGES), eq(50));
        verify(progressService, times(1)).updateProgress(eq(subtitlesId), eq(GenerationProgressStage.IMAGES), eq(100));

        ArgumentCaptor<VideoGeneratorProcessingContext> captorSubtitles = ArgumentCaptor.forClass(VideoGeneratorProcessingContext.class);
        verify(videoGenerator, times(1)).generateVideoFromImages(captorSubtitles.capture());
        assertVideoProcessingContext(captorSubtitles.getValue(), videoFileEntity);

        ArgumentCaptor<VideoGeneratorProcessingContext> captorCombined = ArgumentCaptor.forClass(VideoGeneratorProcessingContext.class);
        verify(videoGenerator, times(1)).combineOriginalVideoWithSubtitles(captorCombined.capture(), eq(subtitlesId));
        assertVideoProcessingContext(captorCombined.getValue(), videoFileEntity);

        verify(progressService, times(1)).updateProgressDone(eq(subtitlesId));
    }

    @Test
    public void run_errorWhenGeneratingVideo_expectErrorTest() throws Exception {
        final String expectedErrorMessage = "Error occurred when generating subtitles for id 1: Error";
        doNothing().when(subtitlesService).setStage(anyLong(), any(VideoFileStatus.class));

        final VideoFileEntity videoFileEntity = TestData.videoFileEntity();
        videoFileEntity.setFilePath(getExampleFilePath());
        when(fileService.getFile(eq(subtitlesId), eq(VideoType.ORIGINAL))).thenReturn(videoFileEntity);

        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(subtitlesService.getDbEntryById(eq(subtitlesId))).thenReturn(subtitles);
        when(subtitlesService.saveEntry(any(SubtitlesEntity.class)))
                .thenAnswer((Answer<SubtitlesEntity>) invocation -> (SubtitlesEntity) invocation.getArguments()[0]);

        doNothing().when(progressService).updateProgress(anyLong(), any(GenerationProgressStage.class), anyInt());
        doThrow(new Exception("Error")).when(videoGenerator).generateVideoFromImages(any(VideoGeneratorProcessingContext.class));

        SubtitlesProcessingTask task = createTestedClass();
        assertThatThrownBy(() -> task.run())
                .isInstanceOf(RuntimeException.class)
                .hasMessage(expectedErrorMessage);

        verify(progressService, times(1)).updateProgressProcessFailed(eq(subtitlesId));
        verify(subtitlesService, times(1)).setStageToError(eq(subtitlesId), eq(expectedErrorMessage));
    }

    @Test
    public void run_errorWhenGettingHeightAndWidth_expectErrorTest() throws Exception {
        final String expectedErrorMessage = "Error occurred when getting height and width for subtitles for id 1:";
        doNothing().when(subtitlesService).setStage(anyLong(), any(VideoFileStatus.class));

        final VideoFileEntity videoFileEntity = TestData.videoFileEntity();
        when(fileService.getFile(eq(subtitlesId), eq(VideoType.ORIGINAL))).thenReturn(videoFileEntity);

        SubtitlesProcessingTask task = createTestedClass();
        assertThatThrownBy(() -> task.run())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(expectedErrorMessage);

        verify(progressService, times(1)).updateProgressProcessFailed(eq(subtitlesId));
        verify(subtitlesService, times(1)).setStageToError(eq(subtitlesId), contains(expectedErrorMessage));
    }

    private void assertVideoProcessingContext(VideoGeneratorProcessingContext context, VideoFileEntity videoFileEntity) {
        assertThat(context).isNotNull();
        assertThat(context.dbVideoFileId()).isEqualTo(subtitlesId);
        assertThat(context.originalFileName()).isEqualTo(videoFileEntity.getFileName());
        assertThat(context.width()).isEqualTo(1920);
        assertThat(context.height()).isEqualTo(1080);
        assertThat(context.imagesWithFrameCoordinates()).hasSize(2);

        VideoGeneratorProcessingContext.ImageWithFrameCoordinates first = context.imagesWithFrameCoordinates().getFirst();
        assertThat(first.image()).isNotNull();
        assertThat(first.startFrame() == 0 || first.startFrame() == 100).isTrue();
        assertThat(first.endFrame() == 100 || first.endFrame() == 200).isTrue();

        VideoGeneratorProcessingContext.ImageWithFrameCoordinates last = context.imagesWithFrameCoordinates().getLast();
        assertThat(last.image()).isNotNull();
        assertThat(last.startFrame() == 0 || last.startFrame() == 100).isTrue();
        assertThat(last.endFrame() == 100 || last.endFrame() == 200).isTrue();
    }

    private String getExampleFilePath() throws IOException {
        File file = File.createTempFile("temp-", ".tmp");
        filesToDelete.add(file);

        InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("white.mp4"));
        try (in; OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(in, out);
        }
        return file.getPath();
    }

    private SubtitlesProcessingTask createTestedClass() {
        return new SubtitlesProcessingTaskBuilder()
                .withVideoGenerator(videoGenerator)
                .withFileService(fileService)
                .withSubtitlesService(subtitlesService)
                .withProgressService(progressService)
                .withSubtitlesId(subtitlesId)
                .build();
    }
}