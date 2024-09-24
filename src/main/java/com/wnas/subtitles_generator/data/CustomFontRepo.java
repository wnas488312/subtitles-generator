package com.wnas.subtitles_generator.data;

import com.wnas.subtitles_generator.data.entity.CustomFontEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFontRepo extends CrudRepository<CustomFontEntity, String> {
}
