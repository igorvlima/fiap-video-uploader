package com.example.fiapvideouploader.application.mapper;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import com.example.fiapvideouploader.domain.model.VideoId;
import com.example.fiapvideouploader.domain.model.VideoMetadata;
import com.example.fiapvideouploader.domain.model.VideoStatus;
import com.example.fiapvideouploader.domain.port.in.UploadVideoCommand;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapperTest {

    private static final UUID VIDEO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String CUSTOMER_EMAIL = "test@example.com";
    private static final String VIDEO_NAME = "test-video.mp4";
    private static final String S3_KEY = "videos/test-key";
    private static final Long CUSTOMER_ID = 123L;
    private static final String GENERATED_NAME = "generated-name.mp4";

    @Test
    void buildVideoProcessorMessage_shouldCreateCorrectMessage() {
        // When
        VideoProcessorMessage message = Mapper.buildVideoProcessorMessage(
                VIDEO_ID, CUSTOMER_EMAIL, VIDEO_NAME, S3_KEY);

        // Then
        assertThat(message).isNotNull();
        assertThat(message.getVideoId()).isEqualTo(VIDEO_ID.toString());
        assertThat(message.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(message.getVideoName()).isEqualTo(VIDEO_NAME);
        assertThat(message.getS3Key()).isEqualTo(S3_KEY);
    }

    @Test
    void buildVideoStatusMessage_fromMetadata_shouldCreateCorrectMessage() {
        // Given
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(new VideoId(VIDEO_ID))
                .originalName(VIDEO_NAME)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // When
        VideoStatusMessage message = Mapper.buildVideoStatusMessage(
                metadata, VideoStatus.UPLOADED.toString());

        // Then
        assertThat(message).isNotNull();
        assertThat(message.getVideoId()).isEqualTo(VIDEO_ID);
        assertThat(message.getVideoName()).isEqualTo(VIDEO_NAME);
        assertThat(message.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(message.getVideoStatus()).isEqualTo(VideoStatus.UPLOADED.toString());
    }

    @Test
    void buildVideoStatusMessage_fromCommand_shouldCreateCorrectMessage() {
        // Given
        UploadVideoCommand command = mock(UploadVideoCommand.class);
        when(command.getFileName()).thenReturn(VIDEO_NAME);
        when(command.getCustomerEmail()).thenReturn(CUSTOMER_EMAIL);

        // When
        VideoStatusMessage message = Mapper.buildVideoStatusMessage(
                command, VIDEO_ID, VideoStatus.ERROR.toString());

        // Then
        assertThat(message).isNotNull();
        assertThat(message.getVideoId()).isEqualTo(VIDEO_ID);
        assertThat(message.getVideoName()).isEqualTo(VIDEO_NAME);
        assertThat(message.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(message.getVideoStatus()).isEqualTo(VideoStatus.ERROR.toString());
    }

    @Test
    void buildVideoDataMessage_shouldCreateCorrectMessage() {
        // Given
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(new VideoId(VIDEO_ID))
                .originalName(VIDEO_NAME)
                .generatedName(GENERATED_NAME)
                .customerId(CUSTOMER_ID)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // When
        VideoDataMessage message = Mapper.buildVideoDataMessage(metadata);

        // Then
        assertThat(message).isNotNull();
        assertThat(message.getVideoId()).isEqualTo(VIDEO_ID);
        assertThat(message.getOriginalName()).isEqualTo(VIDEO_NAME);
        assertThat(message.getGeneratedName()).isEqualTo(GENERATED_NAME);
        assertThat(message.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(message.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
    }
}