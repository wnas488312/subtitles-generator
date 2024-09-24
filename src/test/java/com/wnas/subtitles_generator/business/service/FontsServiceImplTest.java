package com.wnas.subtitles_generator.business.service;

import com.wnas.subtitles_generator.data.CustomFontRepo;
import com.wnas.subtitles_generator.data.entity.CustomFontEntity;
import com.wnas.subtitles_generator.exception.NotFoundException;
import com.wnas.subtitles_generator.testData.TestData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;

class FontsServiceImplTest {
    // Place name of added custom font here before enabling loadCustomFontsTest() test
    private final static String AVAILABLE_FONT_NAME = "";

    @Test
    void getDefaultFontsNamesTest() {
        final CustomFontRepo repoMock = Mockito.mock(CustomFontRepo.class);
        List<String> defaultFontsNames = new FontsServiceImpl(repoMock).getDefaultFontsNames();
        assertThat(defaultFontsNames).isNotEmpty();

        // Some most popular ones
        assertThat(defaultFontsNames).contains("Arial");
        assertThat(defaultFontsNames).contains("Serif");
        assertThat(defaultFontsNames).contains("Times New Roman");
    }

    @Test
    void getDefaultFontsNames_customFontTest() {
        final String fontUsedAsCustom = "Arial";

        final CustomFontRepo repoMock = Mockito.mock(CustomFontRepo.class);
        Mockito.when(repoMock.findAll()).thenReturn(List.of(TestData.customFontEntity(fontUsedAsCustom)));

        List<String> defaultFontsNames = new FontsServiceImpl(repoMock).getDefaultFontsNames();
        assertThat(defaultFontsNames).isNotEmpty();

        assertThat(defaultFontsNames).contains(String.format("CUSTOM_%s", fontUsedAsCustom));
    }

    @Test
    @Disabled
    void loadCustomFontsTest() {
        final CustomFontRepo repoMock = Mockito.mock(CustomFontRepo.class);
        Mockito.when(repoMock.save(any(CustomFontEntity.class))).thenReturn(new CustomFontEntity());

        new FontsServiceImpl(repoMock).loadCustomFonts();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        final Optional<String> foundCustomFont = Arrays.stream(allFonts)
                .map(Font::getFontName)
                .filter(fontName -> fontName.startsWith(AVAILABLE_FONT_NAME))
                .findFirst();
        assertThat(foundCustomFont).isNotEmpty();
    }

    @Test
    void getCustomFontPathTest() {
        final CustomFontEntity fontEntity = TestData.customFontEntity("Custom font");

        final CustomFontRepo repoMock = Mockito.mock(CustomFontRepo.class);
        Mockito.when(repoMock.findById(eq(fontEntity.getFontName()))).thenReturn(Optional.of(fontEntity));

        final String customFontPath = new FontsServiceImpl(repoMock).getCustomFontPath(fontEntity.getFontName());
        assertThat(customFontPath).isNotNull();
        assertThat(customFontPath).isEqualTo(fontEntity.getFilePath());
    }

    @Test
    void getCustomFontPath_notFound_expectErrorTest() {
        final CustomFontRepo repoMock = Mockito.mock(CustomFontRepo.class);
        Mockito.when(repoMock.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new FontsServiceImpl(repoMock).getCustomFontPath("Unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Font with name Unknown is not present in DB.");
    }
}