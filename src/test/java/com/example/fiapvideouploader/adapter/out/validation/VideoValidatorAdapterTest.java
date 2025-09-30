package com.example.fiapvideouploader.adapter.out.validation;

import com.example.fiapvideouploader.domain.exception.VideoValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class VideoValidatorAdapterTest {

    private VideoValidatorAdapter videoValidatorAdapter;

    @BeforeEach
    void setUp() {
        videoValidatorAdapter = new VideoValidatorAdapter();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "video.mp4",
            "test-video.avi",
            "my_movie.mov",
            "sample.mkv",
            "video.wmv",
            "animation.flv",
            "presentation.webm"
    })
    void validate_shouldNotThrowException_whenFileNameHasValidExtension(String fileName) {
        // When/Then
        videoValidatorAdapter.validate(fileName, "video/mp4");
        // No exception should be thrown
    }

    @Test
    void validate_shouldNotThrowException_whenContentTypeIsValid() {
        // Given
        String fileName = "video.mp4";
        String contentType = "video/mp4";

        // When/Then
        videoValidatorAdapter.validate(fileName, contentType);
        // No exception should be thrown
    }

    @Test
    void validate_shouldThrowException_whenFileNameIsNull() {
        // When/Then
        assertThatThrownBy(() -> videoValidatorAdapter.validate(null, "video/mp4"))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("Nome do arquivo inválido");
    }

    @Test
    void validate_shouldThrowException_whenFileNameIsEmpty() {
        // When/Then
        assertThatThrownBy(() -> videoValidatorAdapter.validate("", "video/mp4"))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("Nome do arquivo inválido");
    }

    @Test
    void validate_shouldThrowException_whenFileNameHasInvalidExtension() {
        // Given
        String fileName = "document.pdf";

        // When/Then
        assertThatThrownBy(() -> videoValidatorAdapter.validate(fileName, "application/pdf"))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("Formato de arquivo não suportado");
    }

    @Test
    void validate_shouldNotThrowException_whenContentTypeIsInvalid() {
        // Given
        String fileName = "video.mp4";
        String contentType = "application/octet-stream";

        // When/Then
        videoValidatorAdapter.validate(fileName, contentType);
        // No exception should be thrown, only a warning is logged
    }

    @Test
    void validate_shouldNotThrowException_whenContentTypeIsNull() {
        // Given
        String fileName = "video.mp4";

        // When/Then
        videoValidatorAdapter.validate(fileName, null);
        // No exception should be thrown, only a warning is logged
    }

    @Test
    void validate_shouldBeCaseInsensitive_forFileExtensions() {
        // Given
        String fileName = "video.MP4";

        // When/Then
        videoValidatorAdapter.validate(fileName, "video/mp4");
        // No exception should be thrown
    }
}