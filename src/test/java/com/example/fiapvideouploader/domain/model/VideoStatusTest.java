package com.example.fiapvideouploader.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoStatusTest {

    @Test
    void videoStatus_shouldHaveExpectedValues() {
        // Then
        assertThat(VideoStatus.values()).containsExactly(
                VideoStatus.UPLOADED,
                VideoStatus.PROCESSING,
                VideoStatus.COMPLETED,
                VideoStatus.ERROR
        );
    }

    @Test
    void toString_shouldReturnExpectedStringRepresentation() {
        // Then
        assertThat(VideoStatus.UPLOADED.toString()).isEqualTo("UPLOADED");
        assertThat(VideoStatus.PROCESSING.toString()).isEqualTo("PROCESSING");
        assertThat(VideoStatus.COMPLETED.toString()).isEqualTo("COMPLETED");
        assertThat(VideoStatus.ERROR.toString()).isEqualTo("ERROR");
    }

    @Test
    void valueOf_shouldReturnCorrectEnumValue() {
        // Then
        assertThat(VideoStatus.valueOf("UPLOADED")).isEqualTo(VideoStatus.UPLOADED);
        assertThat(VideoStatus.valueOf("PROCESSING")).isEqualTo(VideoStatus.PROCESSING);
        assertThat(VideoStatus.valueOf("COMPLETED")).isEqualTo(VideoStatus.COMPLETED);
        assertThat(VideoStatus.valueOf("ERROR")).isEqualTo(VideoStatus.ERROR);
    }
}