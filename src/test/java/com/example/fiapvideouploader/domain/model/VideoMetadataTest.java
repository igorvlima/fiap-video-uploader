package com.example.fiapvideouploader.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VideoMetadataTest {

    @Test
    void builder_shouldCreateVideoMetadataWithAllProperties() {
        // Given
        VideoId videoId = new VideoId(UUID.randomUUID());
        String originalName = "test-video.mp4";
        String generatedName = "generated-name.mp4";
        Long customerId = 123L;
        String customerEmail = "test@example.com";
        LocalDateTime timestamp = LocalDateTime.now();
        long fileSize = 1024L;
        String contentType = "video/mp4";

        // When
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(videoId)
                .originalName(originalName)
                .generatedName(generatedName)
                .customerId(customerId)
                .customerEmail(customerEmail)
                .timestamp(timestamp)
                .fileSize(fileSize)
                .contentType(contentType)
                .build();

        // Then
        assertThat(metadata.getVideoId()).isEqualTo(videoId);
        assertThat(metadata.getOriginalName()).isEqualTo(originalName);
        assertThat(metadata.getGeneratedName()).isEqualTo(generatedName);
        assertThat(metadata.getCustomerId()).isEqualTo(customerId);
        assertThat(metadata.getCustomerEmail()).isEqualTo(customerEmail);
        assertThat(metadata.getTimestamp()).isEqualTo(timestamp);
        assertThat(metadata.getFileSize()).isEqualTo(fileSize);
        assertThat(metadata.getContentType()).isEqualTo(contentType);
    }

    @Test
    void builder_shouldAllowNullableFields() {
        // Given
        VideoId videoId = new VideoId(UUID.randomUUID());

        // When
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(videoId)
                .build();

        // Then
        assertThat(metadata.getVideoId()).isEqualTo(videoId);
        assertThat(metadata.getOriginalName()).isNull();
        assertThat(metadata.getGeneratedName()).isNull();
        assertThat(metadata.getCustomerId()).isNull();
        assertThat(metadata.getCustomerEmail()).isNull();
        assertThat(metadata.getTimestamp()).isNull();
        assertThat(metadata.getFileSize()).isEqualTo(0L); // Default value for primitive long
        assertThat(metadata.getContentType()).isNull();
    }
}