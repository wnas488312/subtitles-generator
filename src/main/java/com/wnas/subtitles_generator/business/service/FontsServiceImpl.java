package com.wnas.subtitles_generator.business.service;

import com.wnas.subtitles_generator.data.CustomFontRepo;
import com.wnas.subtitles_generator.data.entity.CustomFontEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class FontsServiceImpl implements FontsService {
    private static final String CUSTOM_FONT_PREFIX = "CUSTOM_";
    private static final String FONT_FILE_EXTENSION = ".ttf";
    private static final String FONTS_FOLDER_NAME= "fonts";

    private final CustomFontRepo fontRepo;

    public FontsServiceImpl(CustomFontRepo fontRepo) {
        this.fontRepo = fontRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDefaultFontsNames() {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fontNames = graphicsEnvironment.getAvailableFontFamilyNames();
        final List<String> fontNamesList = new ArrayList<>();

        final List<CustomFontEntity> savedCustomFonts = new ArrayList<>();
        fontRepo.findAll().forEach(savedCustomFonts::add);

        for (String fontName: fontNames) {
            if (savedCustomFonts.stream().anyMatch(entity -> StringUtils.contains(fontName, entity.getFontName()))) {
                fontNamesList.add(String.format("%s%s", CUSTOM_FONT_PREFIX, fontName));
            } else {
                fontNamesList.add(fontName);
            }
        }

        return fontNamesList;
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

        for (File fontFile: fonts) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                boolean success = ge.registerFont(font);

                if (success) {
                    final CustomFontEntity fontEntity = new CustomFontEntity();
                    fontEntity.setFontName(font.getFamily());
                    fontEntity.setFilePath(fontFile.getPath());
                    fontRepo.save(fontEntity);
                    log.info("Font {} loaded successfully", font.getFamily());
                } else {
                    log.warn("Font {} cannot be loaded", font.getFamily());
                }
            } catch (IOException | FontFormatException e) {
                log.error("Error occurred when loading font from file: {}", fontFile.getName(), e);
            }
        }
    }
}
