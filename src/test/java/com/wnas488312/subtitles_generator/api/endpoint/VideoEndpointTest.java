package com.wnas488312.subtitles_generator.api.endpoint;

import com.wnas488312.subtitles_generator.api.model.VideoType;
import com.wnas488312.subtitles_generator.business.service.FileService;
import com.wnas488312.subtitles_generator.config.properties.AppConfig;
import com.wnas488312.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileType;
import com.wnas488312.subtitles_generator.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoEndpointTest {

    private static final Long IDENTIFIER = 1L;
    private final String pathToFile = Objects.requireNonNull(getClass()
                    .getClassLoader()
                    .getResource("white.mp4"))
            .getPath();

    @Mock
    private AppConfig config;
    @Mock
    private FileService service;

    @InjectMocks
    private VideoEndpoint endpoint;

    @Test
    void downloadFileTest() throws IOException {
        final VideoFileEntity videoFileEntity = createVideoFileEntity();

        when(service.getFile(IDENTIFIER, VideoType.SUBTITLES)).thenReturn(videoFileEntity);
        when(config.getVideoFormat()).thenReturn("mp4");

        ResponseEntity<Resource> response = endpoint.downloadFile(IDENTIFIER, VideoType.SUBTITLES);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentDisposition()).isEqualTo(ContentDisposition.parse("attachment;filename=white.mp4"));
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
        assertThat(response.getHeaders().getContentLength()).isEqualTo(1206343L);
    }

    @Test
    void downloadFileTest_idIsNull_expectError(){
        assertThatThrownBy(() -> endpoint.downloadFile(null, VideoType.SUBTITLES))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Subtitles identifier cannot be null");
    }

    @Test
    void downloadFileTest_fileTypeIsNull_expectError(){
        assertThatThrownBy(() -> endpoint.downloadFile(IDENTIFIER, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Video file type cannot be null");
    }

    @Test
    void downloadFileTest_fileDoesNotExist_expectError(){
        final VideoFileEntity videoFileEntity = createVideoFileEntity();
        videoFileEntity.setFilePath("path/To.File");

        when(service.getFile(IDENTIFIER, VideoType.SUBTITLES)).thenReturn(videoFileEntity);

        assertThatThrownBy(() -> endpoint.downloadFile(IDENTIFIER, VideoType.SUBTITLES))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subtitles with id 1 exists in database, but file cannot be found.");
    }

    private VideoFileEntity createVideoFileEntity() {
        final VideoFileEntity videoFileEntity = new VideoFileEntity();
        videoFileEntity.setFileType(VideoFileType.SUBTITLES);
        videoFileEntity.setFilePath(pathToFile);
        videoFileEntity.setFileName("white");
        videoFileEntity.setId(12L);
        videoFileEntity.setSubtitlesId(IDENTIFIER);
        return videoFileEntity;
    }
}