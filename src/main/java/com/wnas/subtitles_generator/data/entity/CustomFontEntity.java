package com.wnas.subtitles_generator.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "custom_font")
@Getter
@Setter
public class CustomFontEntity {
    @Id
    @Column(name = "font_name")
    private Long fontName;

    @Column(name = "file_path")
    private Long filePath;
}
