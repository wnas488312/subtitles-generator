package com.wnas.subtitles_generator.business.service;

import com.wnas.subtitles_generator.api.model.RgbColorObject;
import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas.subtitles_generator.data.SubtitlesRepo;
import com.wnas.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas.subtitles_generator.data.entity.TextChunk;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import com.wnas.subtitles_generator.exception.NotFoundException;
import com.wnas.subtitles_generator.testData.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtitlesServiceImplTest {

    @Mock
    private SubtitlesRepo repo;
    @Mock
    private FileService fileService;
    @Mock
    private Converter<SubtitlesTextChunk, TextChunk> textChunksConverter;

    @InjectMocks
    private SubtitlesServiceImpl service;

    @Test
    void createNewDbEntryTest() {
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.save(any())).thenReturn(subtitles);

        Long id = service.createNewDbEntry();

        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(subtitles.getId());

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());

        SubtitlesEntity saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(VideoFileStatus.QUEUED);
        assertThat(saved.getCreationDate()).isNotNull();
    }

    @Test
    void updateSubtitlesPropertiesFromRequestTest() {
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.save(any())).thenReturn(subtitles);
        when(repo.findById(any())).thenReturn(Optional.of(subtitles));

        final TextChunk textChunk = TestData.textChunk();
        when(textChunksConverter.convert(any())).thenReturn(textChunk);

        final UpdatePropertiesRequest request = TestData.updatePropertiesRequest();

        SubtitlesEntity updated = service.updateSubtitlesPropertiesFromRequest(subtitles.getId(), request);

        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo(subtitles);

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());
        SubtitlesEntity saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getBottomMargin()).isEqualTo(request.bottomMargin());
        assertThat(saved.getFontName()).isEqualTo(request.fontName());
        assertThat(saved.getOutlineInPixels()).isEqualTo(request.outlineInPixels());
        assertThat(saved.getTextChunks()).hasSize(subtitles.getTextChunks().size());
        assertThat(saved.getTextChunks().getFirst()).isEqualTo(textChunk);
        assertThat(saved.getTextChunks().getLast()).isEqualTo(textChunk);
    }

    @Test
    void updateSubtitlesPropertiesFromRequest_bottomMarginIsNull_expectOkTest() {
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.save(any())).thenReturn(subtitles);
        when(repo.findById(any())).thenReturn(Optional.of(subtitles));

        final TextChunk textChunk = TestData.textChunk();
        when(textChunksConverter.convert(any())).thenReturn(textChunk);

        UpdatePropertiesRequest request = TestData.updatePropertiesRequest();
        request = new UpdatePropertiesRequest(null, request.fontName(), new RgbColorObject(0, 0, 0), 24, request.textChunks(), 3);

        SubtitlesEntity updated = service.updateSubtitlesPropertiesFromRequest(subtitles.getId(), request);

        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo(subtitles);

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());
        SubtitlesEntity saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getBottomMargin()).isEqualTo(subtitles.getBottomMargin());
    }

    @Test
    void updateSubtitlesPropertiesFromRequest_fontNameIsNull_expectOkTest() {
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.save(any())).thenReturn(subtitles);
        when(repo.findById(any())).thenReturn(Optional.of(subtitles));

        final TextChunk textChunk = TestData.textChunk();
        when(textChunksConverter.convert(any())).thenReturn(textChunk);

        UpdatePropertiesRequest request = TestData.updatePropertiesRequest();
        request = new UpdatePropertiesRequest(request.bottomMargin(), null, new RgbColorObject(0, 0, 0), 24, request.textChunks(), 3);

        SubtitlesEntity updated = service.updateSubtitlesPropertiesFromRequest(subtitles.getId(), request);

        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo(subtitles);

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());
        SubtitlesEntity saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getFontName()).isEqualTo(subtitles.getFontName());
    }

    @Test
    void removeSubtitlesTest() throws IOException {
        final Long id = 1L;

        when(repo.existsById(eq(id))).thenReturn(true);
        doNothing().when(repo).deleteById(eq(id));
        doNothing().when(fileService).removeFiles(eq(id));

        service.removeSubtitles(id);

        verify(repo, times(1)).deleteById(eq(id));
        verify(fileService, times(1)).removeFiles(eq(id));
    }

    @Test
    void removeSubtitles_ioException_expectErrorTest() throws IOException {
        final Long id = 1L;

        when(repo.existsById(eq(id))).thenReturn(true);
        doNothing().when(repo).deleteById(eq(id));
        doThrow(IOException.class).when(fileService).removeFiles(eq(id));

        assertThatThrownBy(() -> service.removeSubtitles(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error occurred when trying to delete files for subtitles with id: 1");
    }

    @Test
    void removeSubtitles_notFound_expectErrorTest() {
        final Long id = 1L;
        when(repo.existsById(eq(id))).thenReturn(false);
        assertThatThrownBy(() -> service.removeSubtitles(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Subtitles entry for id 1 not found.");
    }

    @Test
    void getDbEntryByIdTest() {
        final Long id = 1L;
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.findById(eq(id))).thenReturn(Optional.of(subtitles));

        SubtitlesEntity entry = service.getDbEntryById(id);

        assertThat(entry).isNotNull();
        assertThat(entry).isEqualTo(subtitles);
    }

    @Test
    void getDbEntryById_notFound_expectErrorTest() {
        final Long id = 1L;
        when(repo.findById(eq(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDbEntryById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Subtitles entry for id 1 not found.");
    }

    @Test
    void setStageTest() {
        final Long id = 1L;
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.findById(eq(id))).thenReturn(Optional.of(subtitles));
        when(repo.save(any())).thenReturn(subtitles);

        service.setStage(id, VideoFileStatus.READY);

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());
        SubtitlesEntity saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(VideoFileStatus.READY);
    }

    @Test
    void setStage_notFound_expectOkTest() {
        final Long id = 1L;
        when(repo.findById(eq(id))).thenReturn(Optional.empty());
        service.setStage(id, VideoFileStatus.READY);
        verify(repo, times(0)).save(any());
    }

    @Test
    void setStageToErrorTest() {
        final Long id = 1L;
        final String errorMessage = "Error message";
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.findById(eq(id))).thenReturn(Optional.of(subtitles));
        when(repo.save(any())).thenReturn(subtitles);

        service.setStageToError(id, errorMessage);

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());
        SubtitlesEntity saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(saved.getStatus()).isEqualTo(VideoFileStatus.ERROR);
    }

    @Test
    void setStageToError_notFound_expectOkTest() {
        final Long id = 1L;
        final String errorMessage = "Error message";
        when(repo.findById(eq(id))).thenReturn(Optional.empty());
        service.setStageToError(id, errorMessage);
        verify(repo, times(0)).save(any());
    }

    @Test
    void saveEntryTest() {
        final SubtitlesEntity subtitles = TestData.subtitlesEntity();
        when(repo.save(any())).thenReturn(subtitles);

        SubtitlesEntity saved = service.saveEntry(subtitles);

        assertThat(saved).isNotNull();
        assertThat(saved).isEqualTo(subtitles);

        ArgumentCaptor<SubtitlesEntity> captor = ArgumentCaptor.forClass(SubtitlesEntity.class);
        verify(repo, times(1)).save(captor.capture());
        SubtitlesEntity savedBefore = captor.getValue();

        assertThat(savedBefore).isNotNull();
        assertThat(savedBefore).isEqualTo(subtitles);
    }
}