package com.wnas.subtitles_generator.data.entity;

import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video_file")
@Getter
@Setter
public class VideoFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "subtitles_id")
    private Long subtitlesId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_type")
    @Enumerated(EnumType.ORDINAL)
    private VideoFileType fileType;
}
