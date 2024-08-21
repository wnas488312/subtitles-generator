package com.wnas.subtitles_generator.data.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextChunk {
    private String text;
    private int startFrame;
    private int endFrame;
}
