package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas.subtitles_generator.api.model.response.BasicOkResponse;
import com.wnas.subtitles_generator.api.model.response.CreateVideoResponse;
import com.wnas.subtitles_generator.api.model.response.GetVideoStatusResponse;
import com.wnas.subtitles_generator.api.model.response.UpdatePropertiesResponse;
import com.wnas.subtitles_generator.business.SubtitlesProcessingTask;
import com.wnas.subtitles_generator.business.generator.VideoGenerator;
import com.wnas.subtitles_generator.business.service.FileService;
import com.wnas.subtitles_generator.business.service.ProgressServiceImpl;
import com.wnas.subtitles_generator.business.service.SubtitlesService;
import com.wnas.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas.subtitles_generator.data.entity.TextChunk;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import com.wnas.subtitles_generator.testData.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtitlesEndpointTest {
    private static final Long IDENTIFIER = 1L;
    private static final String BASIC_OK_RESPONSE = "OK";

    @Mock
    private SubtitlesService subtitlesService;
    @Mock
    private FileService fileService;
    @Mock
    private VideoGenerator videoGenerator;
    @Mock
    private ExecutorService executorService;
    @Mock
    private ProgressServiceImpl progressService;
    @Mock
    private Converter<TextChunk, SubtitlesTextChunk> textChunkConverter;

    @InjectMocks
    private SubtitlesEndpoint subtitlesEndpoint;

    @BeforeEach
    public void setup() throws Exception {
        Field executorServiceField = SubtitlesEndpoint.class.getDeclaredField("executorService");
        executorServiceField.setAccessible(true);
        executorServiceField.set(subtitlesEndpoint, executorService);
    }

    @Test
    void initialiseProcessTest() {
        when(subtitlesService.createNewDbEntry()).thenReturn(IDENTIFIER);

        CreateVideoResponse response = subtitlesEndpoint.initialiseProcess();

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(IDENTIFIER);

        verify(subtitlesService, times(1)).createNewDbEntry();
    }

    @Test
    void updateSubtitlesPropertiesTest() {
        UpdatePropertiesRequest request = TestData.updatePropertiesRequest();

        SubtitlesEntity subtitlesEntity = TestData.subtitlesEntity();
        when(subtitlesService.updateSubtitlesPropertiesFromRequest(eq(IDENTIFIER), eq(request)))
                .thenReturn(subtitlesEntity);
        SubtitlesTextChunk subtitlesTextChunk = TestData.subtitlesTextChunk();
        when(textChunkConverter.convert(any())).thenReturn(subtitlesTextChunk);

        UpdatePropertiesResponse response = subtitlesEndpoint.updateSubtitlesProperties(IDENTIFIER, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(subtitlesEntity.getId());
        assertThat(response.width()).isEqualTo(subtitlesEntity.getWidth());
        assertThat(response.height()).isEqualTo(subtitlesEntity.getHeight());
        assertThat(response.bottomMargin()).isEqualTo(subtitlesEntity.getBottomMargin());
        assertThat(response.creationDate()).isEqualTo(subtitlesEntity.getCreationDate());
        assertThat(response.fontName()).isEqualTo(subtitlesEntity.getFontName());

        assertThat(response.textChunks()).hasSize(2);
        assertThat(response.textChunks().getFirst()).isEqualTo(subtitlesTextChunk);
        assertThat(response.textChunks().getLast()).isEqualTo(subtitlesTextChunk);

        verify(subtitlesService, times(1)).updateSubtitlesPropertiesFromRequest(eq(IDENTIFIER), eq(request));
        verify(textChunkConverter, times(2)).convert(any());
    }

    @Test
    void processSubtitlesTest() {
        BasicOkResponse response = subtitlesEndpoint.processSubtitles(IDENTIFIER);

        assertNotNull(response);
        assertThat(response.status()).isEqualTo(BASIC_OK_RESPONSE);

        ArgumentCaptor<Thread> captor = ArgumentCaptor.forClass(Thread.class);
        verify(executorService, times(1)).submit(captor.capture());

        assertThat(captor.getValue()).isInstanceOf(SubtitlesProcessingTask.class);
    }

    @Test
    void getVideoStatusTest() {
        when(subtitlesService.getDbEntryById(eq(IDENTIFIER))).thenReturn(TestData.subtitlesEntity());
        GetVideoStatusResponse response = subtitlesEndpoint.getVideoStatus(IDENTIFIER);

        assertNotNull(response);
        assertThat(response.status()).isEqualTo(VideoFileStatus.PROCESSING);
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void getVideoStatus_errorStageTest() {
        when(subtitlesService.getDbEntryById(eq(IDENTIFIER))).thenReturn(TestData.errorSubtitlesEntity());
        GetVideoStatusResponse response = subtitlesEndpoint.getVideoStatus(IDENTIFIER);

        assertNotNull(response);
        assertThat(response.status()).isEqualTo(VideoFileStatus.ERROR);
        assertThat(response.errorMessage()).isEqualTo(TestData.errorSubtitlesEntity().getErrorMessage());
    }

    @Test
    void deleteVideoTest() {
        doNothing().when(subtitlesService).removeSubtitles(eq(IDENTIFIER));
        BasicOkResponse response = subtitlesEndpoint.deleteVideo(IDENTIFIER);

        assertNotNull(response);
        assertThat(response.status()).isEqualTo(BASIC_OK_RESPONSE);
    }
}