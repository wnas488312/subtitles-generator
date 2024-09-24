package com.wnas.subtitles_generator.api.endpoint;

import com.wnas.subtitles_generator.business.service.FontsService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FontsEndpointTest {

    @Mock
    private FontsService fontsService;

    @InjectMocks
    private FontsEndpoint endpoint;

    private List<File> filesToRemove = new ArrayList<>();

    @AfterEach
    public void cleanUp() {
        filesToRemove.forEach(FileUtils::deleteQuietly);
    }

    @Test
    void getFontTest() throws IOException {
        final String fontName = "Custom";

        final File tempFile = File.createTempFile("SGApp", ".tmp");
        filesToRemove.add(tempFile);

        final String someRandomFileInResources = "image1.png";
        try (InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(someRandomFileInResources));
             OutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }

        when(fontsService.getCustomFontPath(eq(fontName))).thenReturn(tempFile.getPath());
        final ResponseEntity<Resource> response = endpoint.getFont(fontName);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}