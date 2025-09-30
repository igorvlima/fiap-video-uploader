package com.example.fiapvideouploader.adapter.out.messaging.kafka;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoDataProducer;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoStatusProducer;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoToProcessProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaVideoEventPublisherAdapterTest {

    @Mock
    private VideoToProcessProducer videoToProcessProducer;

    @Mock
    private VideoStatusProducer videoStatusProducer;

    @Mock
    private VideoDataProducer videoDataProducer;

    @InjectMocks
    private KafkaVideoEventPublisherAdapter kafkaVideoEventPublisherAdapter;

    @Test
    void publishVideoToProcess_shouldDelegateToProducer() {
        // Given
        VideoProcessorMessage message = VideoProcessorMessage.builder()
                .videoId(UUID.randomUUID().toString())
                .videoName("test-video.mp4")
                .customerEmail("test@example.com")
                .s3Key("videos/test-key")
                .build();

        // When
        kafkaVideoEventPublisherAdapter.publishVideoToProcess(message);

        // Then
        verify(videoToProcessProducer).send(message);
    }

    @Test
    void publishVideoStatus_shouldDelegateToProducer() {
        // Given
        VideoStatusMessage message = VideoStatusMessage.builder()
                .videoId(UUID.randomUUID())
                .videoName("test-video.mp4")
                .customerEmail("test@example.com")
                .videoStatus("UPLOADED")
                .build();

        // When
        kafkaVideoEventPublisherAdapter.publishVideoStatus(message);

        // Then
        verify(videoStatusProducer).send(message);
    }

    @Test
    void publishVideoData_shouldDelegateToProducer() {
        // Given
        VideoDataMessage message = VideoDataMessage.builder()
                .videoId(UUID.randomUUID())
                .originalName("test-video.mp4")
                .generatedName("generated-name.mp4")
                .customerId(123L)
                .customerEmail("test@example.com")
                .build();

        // When
        kafkaVideoEventPublisherAdapter.publishVideoData(message);

        // Then
        verify(videoDataProducer).send(message);
    }
}