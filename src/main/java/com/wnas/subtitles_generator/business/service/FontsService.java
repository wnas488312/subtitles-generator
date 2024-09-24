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

    /**
     * Gets file path of custom font file that is loaded and stored in DB.
     * @param fontName  Name of a font (Family).
     * @return          Path to font file.
     */
    String getCustomFontPath(String fontName);
}
