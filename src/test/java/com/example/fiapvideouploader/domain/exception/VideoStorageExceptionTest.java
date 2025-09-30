package com.example.fiapvideouploader.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoStorageExceptionTest {

    private static final String ERROR_MESSAGE = "Error storing video";
    private static final Exception CAUSE = new RuntimeException("Storage service unavailable");

    @Test
    void constructor_withMessage_shouldCreateExceptionWithMessage() {
        // When
        VideoStorageException exception = new VideoStorageException(ERROR_MESSAGE);

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructor_withMessageAndCause_shouldCreateExceptionWithMessageAndCause() {
        // When
        VideoStorageException exception = new VideoStorageException(ERROR_MESSAGE, CAUSE);

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isEqualTo(CAUSE);
    }
}