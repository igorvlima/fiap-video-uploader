package com.example.fiapvideouploader.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoValidationExceptionTest {

    private static final String ERROR_MESSAGE = "Invalid video format";

    @Test
    void constructor_withMessage_shouldCreateExceptionWithMessage() {
        // When
        VideoValidationException exception = new VideoValidationException(ERROR_MESSAGE);

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }
}