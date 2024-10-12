package com.wnas.subtitles_generator.data.entity;

import com.wnas.subtitles_generator.data.entity.converter.TextChunksConverter;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subtitles")
@Getter
@Setter
@Convert(attributeName = "text_chunks", converter = TextChunksConverter.class)
public class SubtitlesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "bottom_margin")
    private Integer bottomMargin;

    @Column(name = "font_name")
    private String fontName;

    @Column(name = "text_chunks", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<TextChunk> textChunks;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private VideoFileStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "font_size")
    private Integer fontSize;

    @Column(name = "font_color_r")
    private Integer colorR;

    @Column(name = "font_color_g")
    private Integer colorG;

    @Column(name = "font_color_b")
    private Integer colorB;

    @Column(name = "outline_in_pixels")
    private Integer outlineInPixels;

    @Transient
    private ColorEntity color;

    @PostLoad
    public void postLoad() {
        color = new ColorEntity(colorR, colorG, colorB);
    }

    @PreUpdate
    public void prePersist() {
        if (Objects.isNull(color) || Objects.isNull(color.getR()) || Objects.isNull(color.getG()) || Objects.isNull(color.getB())) {
            return;
        }

        colorR = color.getR();
        colorG = color.getG();
        colorB = color.getB();
    }

    public SubtitlesEntity() {
    }
}
