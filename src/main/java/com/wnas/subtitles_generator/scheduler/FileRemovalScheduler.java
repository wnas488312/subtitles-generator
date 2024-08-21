package com.wnas.subtitles_generator.scheduler;

import com.wnas.subtitles_generator.business.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class FileRemovalScheduler {
    private final FileService fileService;

    public FileRemovalScheduler(FileService fileService) {
        this.fileService = fileService;
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void performScheduledCleanup() {
        log.info("Performing scheduled files removal");
        log.info("Files older than one day will be removed");
        try {
            fileService.removeFilesOlderThanOneDay();
        } catch (IOException e) {
            log.error("Error occurred when removing files", e);
            return;
        }
        log.info("Files removal process finished successfully");
    }
}
