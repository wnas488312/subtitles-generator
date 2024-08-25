package com.wnas.subtitles_generator.business.service;
import com.wnas.subtitles_generator.business.service.message.GenerationProgressStage;
import com.wnas.subtitles_generator.business.service.message.ProgressMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wnas.subtitles_generator.config.ProgressWebSocketConfig.TOPIC_ENDPOINT;

@Service
public class ProgressServiceImpl implements ProgressService{
    private static final String DONE_MESSAGE = "Done.";

    private final SimpMessagingTemplate template;

    private final Map<Long, Map<GenerationProgressStage, Integer>> progressStore = new HashMap<>();

    public ProgressServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProgress(Long id, GenerationProgressStage stage, int progress) {
        Map<GenerationProgressStage, Integer> singleProgressStore = progressStore.getOrDefault(id, createSingleProgressStore());
        singleProgressStore.put(stage, progress);
        progressStore.put(id, singleProgressStore);

        final AtomicInteger progressSum = new AtomicInteger(0);
        singleProgressStore.values().forEach(progressSum::addAndGet);
        final float finalProgress = (float) progressSum.get() / singleProgressStore.values().size();

        final String topic = String.format(TOPIC_ENDPOINT, id);
        ProgressMessage message = new ProgressMessage(stage.getStringValue(), finalProgress >= 100? 99: (int) finalProgress);
        template.convertAndSend(topic, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProgressDone(Long id) {
        final String topic = String.format(TOPIC_ENDPOINT, id);
        ProgressMessage message = new ProgressMessage(DONE_MESSAGE, 100);
        template.convertAndSend(topic, message);
    }

    private Map<GenerationProgressStage, Integer> createSingleProgressStore() {
        Map<GenerationProgressStage, Integer> singleProgressStore = new HashMap<>();
        singleProgressStore.put(GenerationProgressStage.IMAGES, 0);
        singleProgressStore.put(GenerationProgressStage.SUBTITLES, 0);
        singleProgressStore.put(GenerationProgressStage.VIDEO, 0);
        singleProgressStore.put(GenerationProgressStage.AUDIO, 0);
        return singleProgressStore;
    }
}
