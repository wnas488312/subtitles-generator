package com.wnas.subtitles_generator.business.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FontsServiceImplTest {
    // Place name of added custom font here before enabling loadCustomFontsTest() test
    private final static String AVAILABLE_FONT_NAME = "";

    @Test
    void getDefaultFontsNamesTest() {
        List<String> defaultFontsNames = new FontsServiceImpl().getDefaultFontsNames();
        assertThat(defaultFontsNames).isNotEmpty();

        // Some most popular ones
        assertThat(defaultFontsNames).contains("Arial");
        assertThat(defaultFontsNames).contains("Serif");
        assertThat(defaultFontsNames).contains("Times New Roman");
    }

    @Test
    @Disabled
    void loadCustomFontsTest() {
        new FontsServiceImpl().loadCustomFonts();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        final Optional<String> foundCustomFont = Arrays.stream(allFonts)
                .map(Font::getFontName)
                .filter(fontName -> fontName.startsWith(AVAILABLE_FONT_NAME))
                .findFirst();
        assertThat(foundCustomFont).isNotEmpty();
    }
}