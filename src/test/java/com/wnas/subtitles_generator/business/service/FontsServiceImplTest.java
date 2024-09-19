package com.wnas.subtitles_generator.business.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FontsServiceImplTest {

    @Test
    void getDefaultFontsNamesTest() {
        List<String> defaultFontsNames = new FontsServiceImpl().getDefaultFontsNames();
        assertThat(defaultFontsNames).isNotEmpty();

        // Some most popular ones
        assertThat(defaultFontsNames).contains("Arial");
        assertThat(defaultFontsNames).contains("Serif");
        assertThat(defaultFontsNames).contains("Times New Roman");
    }
}