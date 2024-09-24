package com.wnas.subtitles_generator.data;

import com.wnas.subtitles_generator.data.entity.SubtitlesEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SubtitlesRepo extends CrudRepository<SubtitlesEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM subtitles WHERE creation_date < NOW() - INTERVAL '1 day'")
    Collection<SubtitlesEntity> findEntriesOlderThanOneDay();
}
