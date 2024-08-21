package com.wnas.subtitles_generator.data;

import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface VideoFileRepo extends CrudRepository<VideoFileEntity, Long> {
    Set<VideoFileEntity> findBySubtitlesId(Long subtitlesId);
}
