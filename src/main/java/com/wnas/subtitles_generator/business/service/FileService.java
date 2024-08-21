package com.wnas.subtitles_generator.business.service;

import com.wnas.subtitles_generator.api.model.VideoType;
import com.wnas.subtitles_generator.data.entity.VideoFileEntity;
import com.wnas.subtitles_generator.data.entity.enumerators.VideoFileType;
import jakarta.annotation.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Service used to interact with video files and corresponding DB data.
 */
public interface FileService {

    /**
     * Creates new temporary file and sets file path in existing DB video entry.
     * Throws NotFoundException when DB entry is not found.
     * @param dbVideoFileId Identifier of existing DB video entry.
     * @param fileName      Name of a file to be saved in database (not required).
     * @param fileType      Type of file to be created (ORIGINAL, SUBTITLES or COMBINED).
     * @return Newly created temp file.
     * @throws IOException When file cannot be created.
     */
    File createFile(Long dbVideoFileId, VideoFileType fileType, @Nullable String fileName) throws IOException;

    /**
     * Deletes every file related to given subtitles generation process.
     * @param entityId      Identifier of a subtitles generation process.
     * @throws IOException  If files cannot be deleted.
     */
    void removeFiles(Long entityId) throws IOException;

    /**
     * Deletes every file and subtitles generation process DB entries that are older than one day.
     * @throws IOException  If files cannot be deleted.
     */
    void removeFilesOlderThanOneDay() throws IOException;

    /**
     * Gets file DB entry for given subtitles generation process by given type.
     * @param subtitlesId   Identifier of a subtitles generation process.
     * @param fileType      Type of file to be returned (ORIGINAL, SUBTITLES or COMBINED).
     * @return DB entry for a video contain path to file among other data.
     */
    VideoFileEntity getFile(Long subtitlesId, VideoType fileType);
}
