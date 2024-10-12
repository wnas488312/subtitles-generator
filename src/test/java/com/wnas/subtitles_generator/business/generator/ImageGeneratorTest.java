package com.wnas.subtitles_generator.business.generator;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class ImageGeneratorTest {

    @Test
    void generateImageTest() {
        final String text = "Lorem ipsum";
        final String fontName = "ARIAL";
        final int width = 1080;
        final int height = 720;
        final int margin = 50;
        final int outline = 3;

        ImageGenerator generator = new ImageGenerator(text, fontName, width, height, margin, 12, new Color(0, 0, 0), outline);

        BufferedImage image = generator.generateImage();
        assertThat(image).isNotNull();
        assertThat(image.getHeight()).isEqualTo(height);
        assertThat(image.getWidth()).isEqualTo(width);
    }
}