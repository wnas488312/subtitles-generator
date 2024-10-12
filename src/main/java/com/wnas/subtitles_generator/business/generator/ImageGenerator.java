package com.wnas.subtitles_generator.business.generator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private final int fontSize;
    private final Color fontColor;
    private final int outlineInPixels;

    /**
     * A public constructor.
     *
     * @param text      Text to be displayed on generated image.
     * @param fontName  Name of a font that will be used.
     * @param width     Width of a final Image.
     * @param height    Height of a final Image.
     * @param margin    Distance in pixels from bottom of a text to bottom of a image
     * @param fontSize  Size of a font in pixels.
     * @param fontColor Color of a fount in RGB.
     */
    public ImageGenerator(String text, String fontName, int width, int height, int margin, int fontSize, Color fontColor, int outlineInPixels) {
        this.text = text;
        this.fontName = fontName;
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.outlineInPixels = outlineInPixels;
    }

    /**
     * Generates image for given text and image parameters
     * and returns temp file with image content inside
     * @return temp file with image content
     */
    public BufferedImage generateImage(){
        Font font = new Font(this.fontName, Font.PLAIN, fontSize);

        BufferedImage image = new BufferedImage(this.width, this.height,  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(fontColor);

        FontMetrics fm = g2d.getFontMetrics();
        final int xTextPosition = (this.width - fm.stringWidth(this.text)) / 2;
        final int yTextPosition = this.height - this.margin - fm.getDescent();
        g2d.drawString(this.text, xTextPosition, yTextPosition);
        g2d.dispose();

        return appendOutlineToSubtitles(image, outlineInPixels);
    }

    private BufferedImage appendOutlineToSubtitles(BufferedImage image, int numberOfIterations) {
        if (numberOfIterations <= 0) {
            return image;
        }

        final Color[][] pixels = new Color[image.getWidth()] [image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                pixels[i][j] = new Color(red, green, blue, alpha);
            }
        }

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                final boolean outOfBounds = i < 1 || j < 1 || i > image.getWidth() - 2 || j > image.getHeight() - 2;

                final boolean nextToNotAlpha = !outOfBounds &&
                        (
                                extractAlphaFromPixels(pixels, i-1, j) != 0 ||
                                extractAlphaFromPixels(pixels, i, j-1) != 0 ||
                                extractAlphaFromPixels(pixels, i + 1, j) != 0 ||
                                extractAlphaFromPixels(pixels, i, j + 1) != 0
                        );

                if (extractAlphaFromPixels(pixels, i, j) == 0 && nextToNotAlpha) {
                    final int rgbUpdated = (255 << 24) | (0);
                    image.setRGB(i, j, rgbUpdated);
                }
            }
        }
        return appendOutlineToSubtitles(image, numberOfIterations - 1);
    }

    private int extractAlphaFromPixels(Color[][] pixels, int x, int y) {
        Color color = pixels[x][y];
        return color.getAlpha();
    }
}
