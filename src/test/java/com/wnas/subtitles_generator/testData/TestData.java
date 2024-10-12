package com.wnas.subtitles_generator.testData;

import com.wnas.subtitles_generator.api.model.RgbColorObject;
import com.wnas.subtitles_generator.api.model.request.SubtitlesTextChunk;
import com.wnas.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas.subtitles_generator.data.entity.*;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class used in tests to create objects filled with data
 */
public class TestData {

    public static UpdatePropertiesRequest updatePropertiesRequest() {
        SubtitlesTextChunk subtitlesTextChunk1 = new SubtitlesTextChunk("Lorem ipsum dolor sit amet", 0, 100);
        SubtitlesTextChunk subtitlesTextChunk2 = new SubtitlesTextChunk("consectetur adipiscing elit", 100, 200);
        return new UpdatePropertiesRequest(50,  "ARIAL", new RgbColorObject(0, 0, 0), 24, List.of(subtitlesTextChunk1, subtitlesTextChunk2), 3);
    }

    public static SubtitlesTextChunk subtitlesTextChunk() {
        return new SubtitlesTextChunk("Lorem ipsum dolor sit amet", 0, 100);
    }

    public static SubtitlesEntity subtitlesEntity() {
        SubtitlesEntity subtitlesEntity = new SubtitlesEntity();
        subtitlesEntity.setId(1L);
        subtitlesEntity.setWidth(1080);
        subtitlesEntity.setHeight(720);
        subtitlesEntity.setBottomMargin(50);
        subtitlesEntity.setFontName("ARIAL");
        subtitlesEntity.setColor(new ColorEntity(0, 0, 0));
        subtitlesEntity.setFontSize(24);
        subtitlesEntity.setStatus(VideoFileStatus.PROCESSING);
        subtitlesEntity.setCreationDate(LocalDateTime.of(2025, 8, 19, 12,20));
        subtitlesEntity.setOutlineInPixels(3);

        TextChunk textChunk1 = textChunk("Lorem ipsum dolor sit amet", 0, 100);
        TextChunk textChunk2 = textChunk("consectetur adipiscing elit", 100, 200);
        subtitlesEntity.setTextChunks(List.of(textChunk1, textChunk2));

        return subtitlesEntity;
    }

    public static SubtitlesEntity errorSubtitlesEntity() {
        SubtitlesEntity subtitlesEntity = new SubtitlesEntity();
        subtitlesEntity.setId(1L);
        subtitlesEntity.setWidth(1080);
        subtitlesEntity.setHeight(720);
        subtitlesEntity.setBottomMargin(50);
        subtitlesEntity.setFontName("ARIAL");
        subtitlesEntity.setStatus(VideoFileStatus.ERROR);
        subtitlesEntity.setErrorMessage("Error occurred");
        subtitlesEntity.setCreationDate(LocalDateTime.of(2025, 8, 19, 12,20));
        return subtitlesEntity;
    }

    public static VideoFileEntity videoFileEntity() {
        VideoFileEntity videoFileEntity = new VideoFileEntity();
        videoFileEntity.setId((long) (Math.random() * 1000));
        videoFileEntity.setFileName("white");
        videoFileEntity.setSubtitlesId(1L);
        videoFileEntity.setFileType(VideoFileType.SUBTITLES);
        videoFileEntity.setFilePath("path/to.file");
        return videoFileEntity;
    }

    public static VideoFileEntity videoFileEntityWithTempFile() throws IOException {
        VideoFileEntity videoFileEntity = videoFileEntity();
        File temp = File.createTempFile("tmp-", ".tmp");
        videoFileEntity.setFilePath(temp.getPath());
        return videoFileEntity;
    }

    public static TextChunk textChunk() {
        return textChunk("Lorem ipsum dolor sit amet", 0, 100);
    }

    public static TextChunk textChunk(String text, int startFrame, int endFrame) {
        TextChunk textChunk = new TextChunk();
        textChunk.setText(text);
        textChunk.setStartFrame(startFrame);
        textChunk.setEndFrame(endFrame);
        return textChunk;
    }

    public static CustomFontEntity customFontEntity(String fontName) {
        final CustomFontEntity fontEntity = new CustomFontEntity();
        fontEntity.setFontName(fontName);
        fontEntity.setFilePath(String.format("path/to/file/%s.ttf", fontName));
        return fontEntity;
    }
}
