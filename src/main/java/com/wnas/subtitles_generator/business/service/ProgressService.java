package com.wnas.subtitles_generator.business.service;

import com.wnas.subtitles_generator.business.service.message.GenerationProgressStage;

/**
 * Service used to calculate overall progress of subtitles generation process and
 * expose it through web socket.
 */
public interface ProgressService {

    /**
     * Updates progress for given stage, calculates overall progress and
     * exposes it through web socket.
     * @param id        Identifier of a subtitles generation process.
     * @param stage     Current stage of a processing.
     * @param progress  Current stage progress in percentage.
     */
    void updateProgress(Long id, GenerationProgressStage stage, int progress);

    /**
     * Sets overall progress to 100%, stage to done and exposes it through web socket.
     * @param id        Identifier of a subtitles generation process.
     */
    void updateProgressDone(Long id);

    /**
     * Sets current stage to error and progress to -1.
     * @param id        Identifier of a subtitles generation process.
     */
    default void updateProgressProcessFailed(Long id) {
        updateProgress(id, GenerationProgressStage.ERROR,-1);
    }
}
