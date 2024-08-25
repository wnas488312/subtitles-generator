package com.wnas.subtitles_generator.testData.dummyclasses;

import lombok.Getter;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class DummyFFmpegFrameRecorder extends FFmpegFrameRecorder {
    private final AtomicInteger recordCalledAmount;
    private final AtomicInteger recordSamplesCalledAmount;
    private final AtomicInteger startCalledAmount;
    private final AtomicInteger stopCalledAmount;
    private final AtomicInteger releaseCalledAmount;
    private AtomicInteger expectedVideoCodec;
    private final boolean shouldThrowException;

    public DummyFFmpegFrameRecorder(String filename, int imageWidth, int imageHeight, boolean shouldThrowException) {
        super(filename, imageWidth, imageHeight);
        this.recordCalledAmount = new AtomicInteger(0);
        this.startCalledAmount = new AtomicInteger(0);
        this.stopCalledAmount = new AtomicInteger(0);
        this.releaseCalledAmount = new AtomicInteger(0);
        this.recordSamplesCalledAmount = new AtomicInteger(0);
        this.shouldThrowException = shouldThrowException;
    }

    @Override
    public boolean recordSamples(Buffer... samples) {
        recordSamplesCalledAmount.incrementAndGet();
        return true;
    }

    @Override
    public void setVideoCodec(int videoCodec) {
        expectedVideoCodec = new AtomicInteger(videoCodec);
    }

    @Override
    public void record(Frame frame){
        recordCalledAmount.incrementAndGet();
    }

    @Override
    public void start() throws Exception {
        if (shouldThrowException) {
            throw new Exception("This is bad!");
        }

        startCalledAmount.incrementAndGet();
    }

    @Override
    public void stop(){
        stopCalledAmount.incrementAndGet();
    }

    @Override
    public void release(){
        releaseCalledAmount.incrementAndGet();
    }
}
