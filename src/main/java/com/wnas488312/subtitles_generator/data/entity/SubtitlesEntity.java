package com.wnas488312.subtitles_generator.data.entity;

import com.wnas488312.subtitles_generator.data.entity.converter.TextChunksConverter;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

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

    public SubtitlesEntity() {
    }
}
