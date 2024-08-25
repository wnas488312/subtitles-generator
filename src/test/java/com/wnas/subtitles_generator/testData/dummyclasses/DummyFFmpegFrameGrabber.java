package com.wnas.subtitles_generator.testData.dummyclasses;

import lombok.Getter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class DummyFFmpegFrameGrabber extends FFmpegFrameGrabber {
    private final static int NUMBER_OF_FRAMES = 202;
    private final AtomicInteger grabImageCalledAmount;
    private final AtomicInteger grabSamplesCalledAmount;
    private final AtomicInteger startCalledAmount;
    private final AtomicInteger stopCalledAmount;
    private final AtomicInteger releaseCalledAmount;
    private final boolean areSamplesNull;

    public DummyFFmpegFrameGrabber(String filename, boolean areSamplesNull) {
        super(filename);
        this.grabImageCalledAmount = new AtomicInteger(0);
        this.grabSamplesCalledAmount = new AtomicInteger(0);
        this.startCalledAmount = new AtomicInteger(0);
        this.stopCalledAmount = new AtomicInteger(0);
        this.releaseCalledAmount = new AtomicInteger(0);
        this.areSamplesNull = areSamplesNull;
    }

    @Override
    public void release(){
        releaseCalledAmount.incrementAndGet();
    }

    @Override
    public int getImageWidth() {
        return 1080;
    }

    @Override
    public int getImageHeight() {
        return 720;
    }

    @Override
    public int getAudioChannels() {
        return 2;
    }

    @Override
    public int getVideoBitrate() {
        return 48;
    }

    @Override
    public double getFrameRate() {
        return 24;
    }

    @Override
    public int getSampleRate() {
        return 48;
    }

    @Override
    public long getLengthInTime() {
        return 654321;
    }

    @Override
    public Frame grabImage() {
        int currentFrame = grabImageCalledAmount.incrementAndGet();
        if (currentFrame > NUMBER_OF_FRAMES) {
            return null;
        }

        try (InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("image1.png"));
             Java2DFrameConverter converter = new Java2DFrameConverter()) {
            BufferedImage image = ImageIO.read(in);
            return converter.convert(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Frame grabSamples() {
        int currentFrame = grabSamplesCalledAmount.incrementAndGet();
        if (currentFrame > NUMBER_OF_FRAMES) {
            return null;
        }

        Frame frame = new Frame();
        if (!areSamplesNull) {
            frame.samples = new Buffer[1];
        }
        return frame;
    }

    @Override
    public void start(){
        startCalledAmount.incrementAndGet();
    }

    @Override
    public void stop(){
        stopCalledAmount.incrementAndGet();
    }
}
