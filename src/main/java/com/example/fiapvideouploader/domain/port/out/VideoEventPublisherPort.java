package com.example.fiapvideouploader.domain.port.out;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;

public interface VideoEventPublisherPort {
    void publishVideoToProcess(VideoProcessorMessage videoProcessorMessage);
    void publishVideoStatus(VideoStatusMessage videoStatusMessage);
    void publishVideoData(VideoDataMessage videoDataMessage);
}
