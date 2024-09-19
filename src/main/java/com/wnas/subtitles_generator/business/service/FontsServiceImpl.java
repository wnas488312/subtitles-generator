package com.wnas.subtitles_generator.business.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

@Service
public class FontsServiceImpl implements FontsService {

    @Override
    public List<String> getDefaultFontsNames() {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fontNames = graphicsEnvironment.getAvailableFontFamilyNames();
        return List.of(fontNames);
    }
}
