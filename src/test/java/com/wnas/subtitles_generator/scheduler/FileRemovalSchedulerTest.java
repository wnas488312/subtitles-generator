package com.wnas.subtitles_generator.scheduler;

import com.wnas.subtitles_generator.business.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileRemovalSchedulerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileRemovalScheduler scheduler;

    @Test
    void performScheduledCleanupTest() throws IOException {
        doNothing().when(fileService).removeFilesOlderThanOneDay();
        scheduler.performScheduledCleanup();
        verify(fileService, times(1)).removeFilesOlderThanOneDay();
    }

    @Test
    void performScheduledCleanup_errorOccurred_expectOkTest() throws IOException {
        doThrow(IOException.class).when(fileService).removeFilesOlderThanOneDay();
        scheduler.performScheduledCleanup();
        verify(fileService, times(1)).removeFilesOlderThanOneDay();
    }
}