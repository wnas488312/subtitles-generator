package com.wnas.subtitles_generator.business.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class FontsServiceImpl implements FontsService {
    private static final String FONT_FILE_EXTENSION = ".ttf";
    private static final String FONTS_FOLDER_NAME= "fonts";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDefaultFontsNames() {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fontNames = graphicsEnvironment.getAvailableFontFamilyNames();
        return List.of(fontNames);
    }

    @PostConstruct
    public void loadCustomFonts() {
        log.info("Loading custom fonts");

        final String pathToFontsFolder = Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource(FONTS_FOLDER_NAME)).getPath();

        final File fontsFolder = new File(pathToFontsFolder);
        final File[] files = Optional.ofNullable(fontsFolder.listFiles())
                .orElseThrow(() -> new RuntimeException("Cannot list font files"));

        final List<File> fonts = Arrays.stream(files)
                .filter(File::isFile)
                .filter(File::exists)
                .filter(file -> file.getName().endsWith(FONT_FILE_EXTENSION))
                .toList();

        for (File font: fonts) {
            try {
                Font bangersFont = Font.createFont(Font.TRUETYPE_FONT, font);

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                boolean success = ge.registerFont(bangersFont);

                if (success) {
                    log.info("Font {} loaded successfully", font.getName());
                } else {
                    log.warn("Font {} cannot be loaded", font.getName());
                }
            } catch (IOException | FontFormatException e) {
                log.error("Error occurred when loading font: {}", font.getName(), e);
            }
        }
    }
}
