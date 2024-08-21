package com.wnas.subtitles_generator.business.service;

import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.config.properties.AppConfig;
import com.wnas.subtitles_generator.data.SubtitlesRepo;
import com.wnas.subtitles_generator.data.VideoFileRepo;
import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;
import com.wnas.subtitles_generator.exception.NotFoundException;
import com.wnas.subtitles_generator.testData.TestData;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.wnas.subtitles_generator.testData.TestData.videoFileEntityWithTempFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {
    private static final Long IDENTIFIER = 1L;

    @Mock
    private AppConfig config;
    @Mock
    private VideoFileRepo fileRepo;
    @Mock
    private SubtitlesRepo subtitlesRepo;
    @Mock
    private Converter<VideoType, VideoFileType> videoTypeConverter;

    @InjectMocks
    private FileServiceImpl fileService;

    private final List<File> filesToDelete = new ArrayList<>();

    @AfterEach
    public void cleanUp() {
        filesToDelete.forEach(FileUtils::deleteQuietly);
    }

    @Test
    void createFileTest() throws IOException {
        when(config.getTempFilePrefix()).thenReturn("tmp");
        when(config.getVideoFormat()).thenReturn("mmp4");
        when(subtitlesRepo.existsById(IDENTIFIER)).thenReturn(true);

        final VideoFileType fileType = VideoFileType.SUBTITLES;
        final String fileName = "white";
        File file = fileService.createFile(IDENTIFIER, fileType, fileName);
        filesToDelete.add(file);

        assertThat(file).isNotNull();
        assertThat(file.exists()).isTrue();

        ArgumentCaptor<VideoFileEntity> captor = ArgumentCaptor.forClass(VideoFileEntity.class);
        verify(fileRepo, times(1)).save(captor.capture());

        VideoFileEntity value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getSubtitlesId()).isEqualTo(IDENTIFIER);
        assertThat(value.getFilePath()).isEqualTo(file.getPath());
        assertThat(value.getFileType()).isEqualTo(fileType);
        assertThat(value.getFileName()).isEqualTo(fileName);
    }

    @Test
    void createFile_nullFileName_expectOkTest() throws IOException {
        when(config.getTempFilePrefix()).thenReturn("tmp");
        when(config.getVideoFormat()).thenReturn("mmp4");
        when(subtitlesRepo.existsById(IDENTIFIER)).thenReturn(true);

        final VideoFileType fileType = VideoFileType.SUBTITLES;
        File file = fileService.createFile(IDENTIFIER, fileType, null);
        filesToDelete.add(file);

        assertThat(file).isNotNull();
        assertThat(file.exists()).isTrue();

        ArgumentCaptor<VideoFileEntity> captor = ArgumentCaptor.forClass(VideoFileEntity.class);
        verify(fileRepo, times(1)).save(captor.capture());

        VideoFileEntity value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getSubtitlesId()).isEqualTo(IDENTIFIER);
        assertThat(value.getFilePath()).isEqualTo(file.getPath());
        assertThat(value.getFileType()).isEqualTo(fileType);
        assertThat(value.getFileName()).isNull();
    }

    @Test
    void createFile_subtitlesDoesNotExists_expectErrorTest(){
        when(subtitlesRepo.existsById(IDENTIFIER)).thenReturn(false);

        final VideoFileType fileType = VideoFileType.SUBTITLES;
        final String fileName = "white";
        assertThatThrownBy(() -> fileService.createFile(IDENTIFIER, fileType, fileName))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Subtitles entry for id 1 not found.");

        verify(fileRepo, times(0)).save(any());
    }

    @Test
    void removeFilesTest() throws IOException {
        VideoFileEntity video1 = videoFileEntityWithTempFile();
        VideoFileEntity video2 = videoFileEntityWithTempFile();

        filesToDelete.add(new File(video1.getFilePath()));
        filesToDelete.add(new File(video2.getFilePath()));

        when(fileRepo.findBySubtitlesId(IDENTIFIER)).thenReturn(Set.of(video1, video2));
        doNothing().when(fileRepo).delete(any());

        fileService.removeFiles(IDENTIFIER);

        verify(fileRepo, times(2)).delete(any(VideoFileEntity.class));
    }

    @Test
    void removeFile_emptyFilesList_expectOkTest() throws IOException {
        when(fileRepo.findBySubtitlesId(IDENTIFIER)).thenReturn(Collections.emptySet());
        fileService.removeFiles(IDENTIFIER);
        verify(fileRepo, times(0)).delete(any(VideoFileEntity.class));
    }

    @Test
    void getFileTest() throws IOException {
        final VideoType videoType = VideoType.SUBTITLES;

        VideoFileEntity video1 = videoFileEntityWithTempFile();
        VideoFileEntity video2 = videoFileEntityWithTempFile();
        video2.setFileType(VideoFileType.ORIGINAL);

        filesToDelete.add(new File(video1.getFilePath()));
        filesToDelete.add(new File(video2.getFilePath()));

        when(videoTypeConverter.convert(eq(videoType))).thenReturn(VideoFileType.SUBTITLES);
        when(fileRepo.findBySubtitlesId(IDENTIFIER)).thenReturn(Set.of(video1, video2));

        VideoFileEntity file = fileService.getFile(IDENTIFIER, videoType);

        assertThat(file).isNotNull();
        assertThat(file).isEqualTo(video1);
    }

    @Test
    void getFile_notFound_expectErrorTest() throws IOException {
        final VideoType videoType = VideoType.SUBTITLES;

        VideoFileEntity video1 = videoFileEntityWithTempFile();
        video1.setFileType(VideoFileType.ORIGINAL);

        filesToDelete.add(new File(video1.getFilePath()));

        when(videoTypeConverter.convert(eq(videoType))).thenReturn(VideoFileType.SUBTITLES);
        when(fileRepo.findBySubtitlesId(IDENTIFIER)).thenReturn(Collections.singleton(video1));

        assertThatThrownBy(() -> fileService.getFile(IDENTIFIER, videoType))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Subtitles entry for id 1 not found.");
    }

    @Test
    void removeFilesOlderThanOneDayTest() throws IOException {
        VideoFileEntity video1 = videoFileEntityWithTempFile();
        VideoFileEntity video2 = videoFileEntityWithTempFile();

        filesToDelete.add(new File(video1.getFilePath()));
        filesToDelete.add(new File(video2.getFilePath()));

        when(subtitlesRepo.findEntriesOlderThanOneDay()).thenReturn(Collections.singleton(TestData.subtitlesEntity()));
        when(fileRepo.findBySubtitlesId(any())).thenReturn(Set.of(video1, video2));

        doNothing().when(fileRepo).deleteAll(any());
        doNothing().when(subtitlesRepo).deleteAll(any());

        fileService.removeFilesOlderThanOneDay();

        verify(fileRepo, times(1)).deleteAll(any());
        verify(subtitlesRepo, times(1)).deleteAll(any());
    }

    @Test
    void removeFilesOlderThanOneDay_entriesNotFound_expectOkTest() throws IOException {
        when(subtitlesRepo.findEntriesOlderThanOneDay()).thenReturn(Collections.emptySet());
        doNothing().when(subtitlesRepo).deleteAll(any());

        fileService.removeFilesOlderThanOneDay();

        verify(fileRepo, times(0)).deleteAll(any());
        verify(subtitlesRepo, times(1)).deleteAll(any());
    }
}