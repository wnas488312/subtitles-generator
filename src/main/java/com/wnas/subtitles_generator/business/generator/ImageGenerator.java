package com.wnas.subtitles_generator.business.generator;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class used to generate images with provided text subtitles chunk.
 * Images contains only text with alpha background.
 */
public class ImageGenerator {
    private final String text;
    private final String fontName;
    private final int width;
    private final int height;
    private final int margin;

    /**
     * A public constructor.
     *
     * @param text      Text to be displayed on generated image.
     * @param fontName  Name of a font that will be used.
     * @param width     Width of a final Image.
     * @param height    Height of a final Image.
     * @param margin    Distance in pixels from bottom of a text to bottom of a image
     */
    public ImageGenerator(String text, String fontName, int width, int height, int margin) {
        this.text = text;
        this.fontName = fontName;
        this.width = width;
        this.height = height;
        this.margin = margin;
    }

    /**
     * Generates image for given text and image parameters
     * and returns temp file with image content inside
     * @return temp file with image content
     */
    public BufferedImage generateImage(){
        Font font = new Font(this.fontName, Font.PLAIN, 24);

        BufferedImage image = new BufferedImage(this.width, this.height,  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        final int xTextPosition = (this.width - fm.stringWidth(this.text)) / 2;
        final int yTextPosition = this.height - this.margin - fm.getDescent();
        g2d.drawString(this.text, xTextPosition, yTextPosition);
        g2d.dispose();

        return image;
    }
}
