package com.example.fiapvideouploader.adapter.out.messaging.kafka;

import com.example.fiapvideouploader.*;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoToProcessProducer;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoStatusProducer;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoDataProducer;
import com.example.fiapvideouploader.domain.model.*;
import com.example.fiapvideouploader.domain.model.VideoMetadata;
import com.example.fiapvideouploader.domain.port.out.VideoEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaVideoEventPublisherAdapter implements VideoEventPublisherPort {

    private final VideoToProcessProducer videoToProcessProducer;
    private final VideoStatusProducer videoStatusProducer;
    private final VideoDataProducer videoDataProducer;

    @Override
    public void publishVideoToProcess(VideoProcessorMessage videoProcessorMessage) {
        videoToProcessProducer.send(videoProcessorMessage);
    }

    @Override
    public void publishVideoStatus(VideoStatusMessage videoStatusMessage) {
        videoStatusProducer.send(videoStatusMessage);
    }

    @Override
    public void publishVideoData( VideoDataMessage videoDataMessage){
        videoDataProducer.send(videoDataMessage);
    }
}