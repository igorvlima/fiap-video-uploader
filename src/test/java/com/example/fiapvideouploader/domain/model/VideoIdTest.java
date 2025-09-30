package com.example.fiapvideouploader.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VideoIdTest {

    @Test
    void generate_shouldCreateNewVideoIdWithRandomUUID() {
        // When
        VideoId videoId = VideoId.generate();

        // Then
        assertThat(videoId).isNotNull();
        assertThat(videoId.getValue()).isNotNull();
        assertThat(videoId.getValue()).isInstanceOf(UUID.class);
    }

    @Test
    void constructor_shouldCreateVideoIdWithGivenUUID() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        VideoId videoId = new VideoId(uuid);

        // Then
        assertThat(videoId.getValue()).isEqualTo(uuid);
    }

    @Test
    void toString_shouldReturnUUIDStringRepresentation() {
        // Given
        UUID uuid = UUID.randomUUID();
        VideoId videoId = new VideoId(uuid);

        // When
        String result = videoId.toString();

        // Then
        assertThat(result).isEqualTo(uuid.toString());
    }
}