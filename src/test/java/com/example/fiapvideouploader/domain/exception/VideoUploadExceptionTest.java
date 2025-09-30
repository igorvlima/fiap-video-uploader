package com.example.fiapvideouploader.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoUploadExceptionTest {

    private static final String ERROR_MESSAGE = "Error during video upload";
    private static final Exception CAUSE = new RuntimeException("Original cause");

    @Test
    void constructor_withMessage_shouldCreateExceptionWithMessage() {
        // When
        VideoUploadException exception = new VideoUploadException(ERROR_MESSAGE);

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructor_withMessageAndCause_shouldCreateExceptionWithMessageAndCause() {
        // When
        VideoUploadException exception = new VideoUploadException(ERROR_MESSAGE, CAUSE);

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isEqualTo(CAUSE);
    }
}