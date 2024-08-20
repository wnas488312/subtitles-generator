package com.wnas488312.subtitles_generator.business.service;

import com.wnas488312.subtitles_generator.api.model.request.UpdatePropertiesRequest;
import com.wnas488312.subtitles_generator.data.entity.SubtitlesEntity;
import com.wnas488312.subtitles_generator.data.entity.enumerators.VideoFileStatus;

/**
 * Service used to read or manipulate data of subtitles generation process.
 */
public interface SubtitlesService {

    /**
     * Creates and saves new DB entry for subtitles generation process,
     * sets status to QUEUED and creation date to now.
     * @return id of a newly created entry.
     */
    Long createNewDbEntry();

    /**
     * Converts update properties request into DB subtitles generation process entry
     * and saves it into the database.
     * @param id        Identifier of a subtitles generation process.
     * @param request   Request of a update properties endpoint.
     * @return          Newly saved DB entry.
     */
    SubtitlesEntity updateSubtitlesPropertiesFromRequest(Long id, UpdatePropertiesRequest request);

    /**
     * Removes subtitles generation process from database and files related to given entry.
     * @param id    Identifier of a subtitles generation process.
     */
    void removeSubtitles(Long id);

    /**
     * Gets subtitles generation process entry from database for provided id, and throws NotFoundException when entry is not found.
     * @param id identifier of a subtitles generation process DB entry.
     * @return Subtitles generation process DB entry.
     */
    SubtitlesEntity getDbEntryById(Long id);

    /**
     * Sets stage in DB subtitles generation process entry for given ID if exists.
     * @param entityId Identifier of DB entry.
     * @param status Status to be set.
     */
    void setStage(Long entityId, VideoFileStatus status);

    /**
     * Sets stage in DB subtitles generation process entry to error
     * and appends error message to DB entry.
     * @param entityId Identifier of DB entry.
     * @param errorMessage Details about occurred error.
     */
    void setStageToError(Long entityId, String errorMessage);

    /**
     * Saves DB subtitles generation process entry in the database.
     * @param entry DB entry to be saved.
     * @return      Newly saved entry.
     */
    SubtitlesEntity saveEntry(SubtitlesEntity entry);
}
