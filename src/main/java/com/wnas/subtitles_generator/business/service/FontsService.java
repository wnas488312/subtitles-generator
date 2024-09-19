package com.wnas.subtitles_generator.business.service;

import java.util.List;

/**
 * Service used to perform actions related to fonts.
 */
public interface FontsService {

    /**
     * Gets names of fonts installed on the system.
     * @return List of fonts names.
     */
    List<String> getDefaultFontsNames();
}
